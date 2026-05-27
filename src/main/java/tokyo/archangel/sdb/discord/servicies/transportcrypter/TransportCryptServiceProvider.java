package tokyo.archangel.sdb.discord.servicies.transportcrypter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class TransportCryptServiceProvider {
	private final ObjectProvider<TransportCryptServiceImpl> provider;

	private ConcurrentMap<String, TransportCryptServiceImpl> cryptServices = new ConcurrentHashMap<>();

	public TransportCryptServiceProvider(ObjectProvider<TransportCryptServiceImpl> provider) {
		this.provider = provider;
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ処理可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param websocketGuid voiceWebsocketのGUID
	 * @param channelId ボットが参加しているチャンネルID
	 * @param userId ボット本体のユーザーID
	 * @param ssrc ssrc
	 * @param channelInfo 音声接続情報
	 * @return E2EEサービス
	 */
	public TransportCryptService generateCryptService(String websocketGuid, byte[] keys) {
		return cryptServices.computeIfAbsent(websocketGuid, id -> {
			TransportCryptServiceImpl service = provider.getObject();
			service.setKey(keys);
			return service;
		});
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを返す
	 * @param channelId
	 * @return 対象のチャンネルIDのE2EEサービス。ただし、見つからない場合NULL。
	 */
	public TransportCryptService getCryptService(String websocketGuid) {
		return cryptServices.get(websocketGuid);
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを破棄する
	 * @param channelId
	 */
	public void removeCryptService(String websocketGuid) {
		cryptServices.remove(websocketGuid);
	}
}
