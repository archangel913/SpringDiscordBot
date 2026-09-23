package tokyo.archangel.sdb.internal.servicies.gateway;

import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

/**
 * {@link StandardWebSocketClient}を生成するデフォルトのプロバイダー実装
 */
public class StandardWebSocketClientProvider implements WebSocketClientProvider {

	@Override
	public WebSocketClient getWebSocketClient() {
		return new StandardWebSocketClient();
	}
}
