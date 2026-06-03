package tokyo.archangel.sdb.discord.servicies.transportcrypter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransportCryptServiceProvider {
	private final ObjectProvider<TransportCryptServiceImpl> provider;

	private final ConcurrentMap<String, TransportCryptServiceImpl> cryptServices = new ConcurrentHashMap<>();

	public TransportCryptServiceProvider(ObjectProvider<TransportCryptServiceImpl> provider) {
		this.provider = provider;
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ処理可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param websocketGuid voiceWebsocketのGUID
	 * @param keys 暗号化キー
	 * @return E2EEサービス
	 */
	public TransportCryptService generateCryptService(String websocketGuid, byte[] keys) {
		if (websocketGuid == null) {
			return null;
		}

		TransportCryptServiceImpl service = cryptServices.get(websocketGuid);
		if (service != null) {
			return service;
		}

		TransportCryptServiceImpl newService = provider.getObject();
		newService.setKey(keys);

		TransportCryptServiceImpl existingService = cryptServices.putIfAbsent(websocketGuid, newService);
		return (existingService == null) ? newService : existingService;
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを返す
	 * @param websocketGuid
	 * @return 対象のチャンネルIDのE2EEサービス。ただし、見つからない場合NULL。
	 */
	public TransportCryptService getCryptService(String websocketGuid) {
		log.debug("現在有効なトランスポート暗号化サービスは" + cryptServices.size() + "個です");
		return cryptServices.get(websocketGuid);
	}

	public void moveCryptService(String oldGuid, String newGuid) {
		TransportCryptServiceImpl service = cryptServices.remove(oldGuid);
		if (service == null) {
			return;
		}
		cryptServices.put(newGuid, service);
		log.debug("現在有効なトランスポート暗号化サービスは" + cryptServices.size() + "個です");
	}

	/**
	 * 渡されたウェブソケットGUIDのトランスポート暗号化サービスを破棄する
	 * @param websocketGuid
	 */
	public void removeCryptService(String websocketGuid) {
		cryptServices.remove(websocketGuid);
		log.debug("トランスポート暗号化サービスを削除しました。現在有効なサービスは" + cryptServices.size() + "個です");
	}
}
