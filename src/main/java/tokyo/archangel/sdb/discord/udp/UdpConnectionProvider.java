package tokyo.archangel.sdb.discord.udp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * UDPコネクションを提供するクラス
 * @author archangel
 */
@Service
@Slf4j
public class UdpConnectionProvider {
	private final ObjectProvider<UdpConnectionImpl> provider;

	private final ConcurrentMap<String, UdpConnectionImpl> udpConnectionServices = new ConcurrentHashMap<>();

	public UdpConnectionProvider(ObjectProvider<UdpConnectionImpl> provider) {
		this.provider = provider;
	}

	/**
	 * 渡されたチャンネルIDのコネクションを生成する<br>
	 * 内部で初期化処理も実施しており、取得後すぐ送受信可能<br>
	 * また、生成済みのチャンネルIDの場合はそのインスタンスを返す。
	 * @param channelId
	 * @param address 送信対象のIPアドレス
	 * @param port 送信対象のポート番号
	 * @return UDPコネクション
	 */
	public UdpConnection generateUdpConnection(String channelId, String address, int port) {
		if (channelId == null) {
			return null;
		}

		UdpConnectionImpl service = udpConnectionServices.get(channelId);
		if (service != null) {
			return service;
		}

		// インスタンス生成と初期化処理
		UdpConnectionImpl newService = provider.getObject();
		try {
			newService.init(address, port);
		} catch (Exception e) {
			log.error("UDPの初期化が失敗しました。", e);
			newService.close();
			return null;
		}
		newService.receive();

		// mapへ登録
		UdpConnectionImpl existingService = udpConnectionServices.putIfAbsent(channelId, newService);

		if (existingService == null) {
			return newService;
		}

		newService.close();
		return existingService;
	}

	/**
	 * 渡されたチャンネルIDのコネクションを返す
	 * @param channelId
	 * @return 対象のチャンネルIDのコネクション。ただし、見つからない場合NULL。
	 */
	public UdpConnection getUdpConnection(String channelId) {
		return udpConnectionServices.get(channelId);
	}

	/**
	 * 渡されたチャンネルIDのコネクションを破棄する
	 * @param channelId
	 */
	public void removeUdpConnection(String channelId) {
		UdpConnectionImpl service = udpConnectionServices.remove(channelId);
		if (service == null) {
			return;
		}
		service.close();
		log.debug("UDPサービスを削除しました。現在有効なサービスは" + udpConnectionServices.size() + "個です");
	}
}
