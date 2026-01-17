package tokyo.archangel.sdb.discord.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayService;

@Component
@Slf4j
public class GatewayWebSocketHandler extends TextWebSocketHandler {
	@Autowired
	private GatewayService discordMainService;
	
	@Autowired
	private ApplicationProperties properties;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 接続時に呼ばれるメソッド
		log.debug("メインWebSocket: 接続されました");
		session.setTextMessageSizeLimit(properties.getWebsocketMessageSizeLimit());
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// テクストを受信したときに呼ばれるメソッド
		String payload = message.getPayload();
		discordMainService.receive(payload, session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 切断時に呼ばれるメソッド
		log.debug("メインWebSocket: 切断されました");
		log.debug(status.getReason());
	}
}
