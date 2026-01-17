package tokyo.archangel.sdb.discord.servicies.gateway;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.websocket.handler.GatewayWebSocketHandler;

@Service
@Slf4j
public class GatewayConnectionService {
	private GatewayWebSocketHandler discordWebSocketHandler;

	public GatewayConnectionService(GatewayWebSocketHandler discordWebSocketHandler) {
		this.discordWebSocketHandler = discordWebSocketHandler;
	}

	public void connect(String url) {
		// websocket生成
		WebSocketClient client = new StandardWebSocketClient();

		log.debug("websocket接続開始");
		client.execute(discordWebSocketHandler, url)
				.whenComplete((session, ex) -> {
					if (Objects.isNull(ex)) {
						log.debug("websocket接続完了");
					} else {
						log.error("websocket接続失敗", ex);
					}
				});
	}
}
