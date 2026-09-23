package tokyo.archangel.sdb.internal.servicies.gateway;

import org.springframework.web.socket.client.WebSocketClient;

/**
 * WebSocketClientの生成を担うプロバイダー<br>
 * 実装を差し替え可能にすることで、接続処理をモック化してテストできるようにする
 */
public interface WebSocketClientProvider {

	/**
	 * WebSocketClientを生成する
	 * @return WebSocketClient
	 */
	public WebSocketClient getWebSocketClient();
}
