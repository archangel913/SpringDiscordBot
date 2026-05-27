package tokyo.archangel.sdb.discord.udp;

import java.net.SocketException;
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

	private ConcurrentMap<String, UdpConnectionImpl> udpConnectionServices = new ConcurrentHashMap<>();

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
		return udpConnectionServices.computeIfAbsent(channelId, id -> {
			UdpConnectionImpl service = provider.getObject();
			try {
				service.init(address, port);
				service.receive();
				return service;
			} catch (SocketException e) {
				log.error("UDPコネクションの初期化に失敗しました", e);
				throw new IllegalStateException(e);
			}
		});
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
	}
}
