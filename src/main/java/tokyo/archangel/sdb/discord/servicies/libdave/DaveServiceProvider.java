package tokyo.archangel.sdb.discord.servicies.libdave;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;

@Service
@Slf4j
public class DaveServiceProvider {
	private final ObjectProvider<DaveServiceImpl> provider;

	private ConcurrentMap<String, DaveServiceImpl> daveServices = new ConcurrentHashMap<>();

	public DaveServiceProvider(ObjectProvider<DaveServiceImpl> provider) {
		this.provider = provider;
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
	public DaveService generateDaveService(String websocketGuid, String channelId, String userId, int ssrc,
			VoiceChannelInfo channelInfo) {
		return daveServices.computeIfAbsent(websocketGuid, id -> {
			DaveServiceImpl service = provider.getObject();
			service.init(channelId, userId, ssrc, channelInfo);
			return service;
		});
	}

	/**
	 * 渡されたウェブソケットGUIDのE2EEサービスを返す
	 * @param channelId
	 * @return 対象のチャンネルIDのE2EEサービス。ただし、見つからない場合NULL。
	 */
	public DaveService getDaveService(String websocketGuid) {
		return daveServices.get(websocketGuid);
	}

	/**
	 * 渡されたウェブソケットGUIDのE2EEサービスを破棄する
	 * @param channelId
	 */
	public void removeDaveService(String websocketGuid) {
		DaveServiceImpl service = daveServices.remove(websocketGuid);
		if (service == null) {
			return;
		}
		service.close();
		log.debug("Daveサービスを削除しました。現在有効なサービスは" + daveServices.size() + "個です");
	}
}
