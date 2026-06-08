package tokyo.archangel.sdb.discord.servicies.voice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.servicies.libdave.E2eeCryptService;
import tokyo.archangel.sdb.discord.servicies.libdave.E2eeCryptServiceImpl;
import tokyo.archangel.sdb.discord.servicies.transportcrypter.TransportCryptService;
import tokyo.archangel.sdb.discord.servicies.transportcrypter.TransportCryptServiceImpl;
import tokyo.archangel.sdb.discord.udp.UdpConnection;
import tokyo.archangel.sdb.discord.udp.UdpConnectionImpl;
import tokyo.archangel.sdb.discord.voice.VoiceBinaryBuffer;

@Service
@Slf4j
public class VoiceSessionProvider {
	private final ObjectProvider<UdpConnectionImpl> udpConnectionProvider;

	private final ObjectProvider<E2eeCryptServiceImpl> e2eeCryptServiceProvider;

	private final ObjectProvider<TransportCryptServiceImpl> transportCryptServiceProvider;

	private final ObjectProvider<VoiceSendServiceImpl> voiceSendServiceProvider;

	private final ConcurrentMap<String, VoiceSession> voiceSessionServices = new ConcurrentHashMap<>();

	public VoiceSessionProvider(ObjectProvider<UdpConnectionImpl> udpConnectionProvider,
			ObjectProvider<E2eeCryptServiceImpl> e2eeCryptServiceProvider,
			ObjectProvider<TransportCryptServiceImpl> transportCryptServiceProvider,
			ObjectProvider<VoiceSendServiceImpl> voiceSendServiceProvider) {
		this.udpConnectionProvider = udpConnectionProvider;
		this.e2eeCryptServiceProvider = e2eeCryptServiceProvider;
		this.transportCryptServiceProvider = transportCryptServiceProvider;
		this.voiceSendServiceProvider = voiceSendServiceProvider;
	}

	/**
	 * 渡されたウェブソケットGUIDのコネクションを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ送受信可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param websocketGuid voiceWebsocketのGUID
	 * @param address 送信対象のIPアドレス
	 * @param port 送信対象のポート番号
	 * @return UDPコネクション
	 */
	public UdpConnection getUdpConnection(String websocketGuid, String channelId, String address, int port)
			throws IllegalStateException {
		if (websocketGuid == null
				|| channelId == null
				|| address == null) {
			throw new IllegalStateException("初期化に必要な情報がありません");
		}

		VoiceSession voiceSession = voiceSessionServices.computeIfAbsent(websocketGuid, k -> new VoiceSession());
		if (voiceSession.getUdpConnection() != null) {
			return voiceSession.getUdpConnection();
		}

		// インスタンス生成と初期化処理
		UdpConnectionImpl newService = udpConnectionProvider.getObject();
		try {
			newService.init(address, port);
		} catch (Exception e) {
			log.error("UDPの初期化が失敗しました。", e);
			newService.close();
			return null;
		}
		newService.receive(channelId);

		// voiceSessionへ設定
		UdpConnectionImpl existingService = voiceSession.compareAndSetUdpConnection(newService);

		// もし他スレッドと同時に動いていて、登録できなかった場合
		if (existingService != newService) {
			newService.close();
		}

		return existingService;
	}

	/**
	 * 渡されたウェブソケットGUIDのE2EEサービスを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ処理可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param websocketGuid voiceWebsocketのGUID
	 * @param channelId ボットが参加しているチャンネルID
	 * @param userId ボット本体のユーザーID
	 * @param ssrc ssrc
	 * @param channelInfo 音声接続情報
	 * @return E2EEサービス
	 */
	public E2eeCryptService getE2eeCryptService(String websocketGuid, String channelId, String userId, int ssrc,
			VoiceChannelInfo channelInfo) throws IllegalStateException {
		if (websocketGuid == null
				|| channelId == null
				|| userId == null
				|| channelInfo == null) {
			throw new IllegalStateException("初期化に必要な情報がありません");
		}

		VoiceSession voiceSession = voiceSessionServices.computeIfAbsent(websocketGuid, k -> new VoiceSession());
		if (voiceSession.getE2eeCryptService() != null) {
			return voiceSession.getE2eeCryptService();
		}

		// インスタンス生成と初期化処理
		E2eeCryptServiceImpl newService = e2eeCryptServiceProvider.getObject();
		newService.init(channelId, userId, ssrc, channelInfo);

		// voiceSessionへ設定
		E2eeCryptServiceImpl existingService = voiceSession.compareAndSetE2eeCryptService(newService);

		// もし他スレッドと同時に動いていて、登録できなかった場合
		if (existingService != newService) {
			newService.close();
		}

		return existingService;
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ処理可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param websocketGuid voiceWebsocketのGUID
	 * @param keys 暗号化キー
	 * @return E2EEサービス
	 */
	public TransportCryptService getTransportCryptService(String websocketGuid, byte[] keys)
			throws IllegalStateException {
		if (websocketGuid == null
				|| keys == null) {
			throw new IllegalStateException("初期化に必要な情報がありません");
		}

		VoiceSession voiceSession = voiceSessionServices.computeIfAbsent(websocketGuid, k -> new VoiceSession());
		if (voiceSession.getTransportCryptService() != null) {
			return voiceSession.getTransportCryptService();
		}

		// インスタンス生成と初期化処理
		TransportCryptServiceImpl newService = transportCryptServiceProvider.getObject();
		newService.setKey(keys);

		// voiceSessionへ設定
		return voiceSession.compareAndSetTransportCryptService(newService);
	}

	/**
	 * 渡されたウェブソケットGUIDの音声送信サービスを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ処理可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param websocketGuid voiceWebsocketのGUID
	 * @return 
	 */
	public VoiceSendService getVoiceSendService(String websocketGuid, VoiceBinaryBuffer buffer,
			VoiceChannelInfo voiceInfo)
			throws IllegalStateException {
		if (websocketGuid == null
				|| buffer == null
				|| voiceInfo == null) {
			throw new IllegalStateException("初期化に必要な情報がありません");
		}

		VoiceSession voiceSession = voiceSessionServices.computeIfAbsent(websocketGuid, k -> new VoiceSession());
		if (voiceSession.getVoiceSendService() != null) {
			return voiceSession.getVoiceSendService();
		}

		// インスタンス生成と初期化処理
		VoiceSendServiceImpl newService = voiceSendServiceProvider.getObject();
		newService.init(buffer, voiceInfo);
		newService.send();

		// voiceSessionへ設定
		VoiceSendServiceImpl existingService = voiceSession.compareAndSetVoiceSendService(newService);
		if (existingService != newService) {
			newService.close();
		}

		return existingService;
	}

	/**
	 * 渡されたウェブソケットGUIDのUDPコネクションを取得する
	 * @param websocketGuid
	 * @return UDPコネクション。取得できなければnull
	 */
	public UdpConnection getUdpConnection(String websocketGuid) {
		if (websocketGuid == null) {
			return null;
		}

		VoiceSession voiceSession = voiceSessionServices.get(websocketGuid);

		if (voiceSession != null && voiceSession.getUdpConnection() != null) {
			return voiceSession.getUdpConnection();
		}
		return null;
	}

	/**
	 * 渡されたウェブソケットGUIDのE2EE暗号化サービスをを取得する
	 * @param websocketGuid
	 * @return E2EE暗号化サービス。取得できなければnull
	 */
	public E2eeCryptService getE2eeCryptService(String websocketGuid) {
		if (websocketGuid == null) {
			return null;
		}

		VoiceSession voiceSession = voiceSessionServices.get(websocketGuid);

		if (voiceSession != null && voiceSession.getE2eeCryptService() != null) {
			return voiceSession.getE2eeCryptService();
		}
		return null;
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを取得する
	 * @param websocketGuid
	 * @return トランスポート暗号化サービス。取得できなければnull
	 */
	public TransportCryptService getTransportCryptService(String websocketGuid) {
		if (websocketGuid == null) {
			return null;
		}

		VoiceSession voiceSession = voiceSessionServices.get(websocketGuid);

		if (voiceSession != null && voiceSession.getTransportCryptService() != null) {
			return voiceSession.getTransportCryptService();
		}
		return null;
	}

	/**
	 * 
	 * @param websocketGuid
	 * @return
	 */
	public VoiceSendService getVoiceSendService(String websocketGuid) {
		if (websocketGuid == null) {
			return null;
		}

		VoiceSession voiceSession = voiceSessionServices.get(websocketGuid);

		if (voiceSession != null && voiceSession.getVoiceSendService() != null) {
			return voiceSession.getVoiceSendService();
		}
		return null;
	}

	/**
	 * サービスのGUIDを付け替える
	 * @param oldGuid
	 * @param newGuid
	 */
	public void moveDaveService(String oldGuid, String newGuid) throws IllegalStateException {
		if (oldGuid == null || newGuid == null) {
			throw new IllegalStateException("キーの付け替えに必要な情報がありません");
		}

		VoiceSession session = voiceSessionServices.remove(oldGuid);
		if (session == null) {
			throw new IllegalStateException("付け替え対象のボイスセッションがありません");
		}
		voiceSessionServices.put(newGuid, session);
	}

	/**
	 * 渡されたウェブソケットGUIDのサービスをすべて破棄する
	 * @param channelId
	 */
	public void removeServicies(String websocketGuid) throws IllegalStateException {
		if (websocketGuid == null) {
			throw new IllegalStateException("キーの削除に必要な情報がありません");
		}

		VoiceSession session = voiceSessionServices.remove(websocketGuid);
		if (session == null) {
			// nullだった場合削除済みとみなす
			return;
		}
		
		VoiceSendServiceImpl voiceSendService = session.getVoiceSendService();
		if (voiceSendService != null) {
			voiceSendService.close();
		}

		UdpConnectionImpl udpConnection = session.getUdpConnection();
		if (udpConnection != null) {
			udpConnection.close();
		}

		E2eeCryptServiceImpl e2eeCryptService = session.getE2eeCryptService();
		if (e2eeCryptService != null) {
			e2eeCryptService.close();
		}

		log.debug("音声サービスを削除しました。現在有効なサービスは{}個です", voiceSessionServices.size());
	}
	
	@PreDestroy
	public void close() {
		
	}
}
