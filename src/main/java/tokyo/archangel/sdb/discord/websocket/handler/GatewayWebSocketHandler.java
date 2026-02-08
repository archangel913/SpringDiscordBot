package tokyo.archangel.sdb.discord.websocket.handler;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.api.DiscordApi;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayConnectionService;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewaySendMessageService;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayService;

@Component
@Slf4j
public class GatewayWebSocketHandler extends TextWebSocketHandler {
	private GatewayService discordMainService;

	private DiscordApi api;

	private ApplicationProperties properties;

	private GatewayInfo gatewayInfo;

	private GatewayConnectionService gatewayConnectionService;
	
	private GatewaySendMessageService gatewaySendMessageService;

	private boolean isShuttingDown = false;

	public GatewayWebSocketHandler(GatewayService discordMainService, DiscordApi api, ApplicationProperties properties,
			GatewayInfo gatewayInfo, @Lazy GatewayConnectionService gatewayConnectionService, GatewaySendMessageService gatewaySendMessageService) {
		this.discordMainService = discordMainService;
		this.api = api;
		this.properties = properties;
		this.gatewayInfo = gatewayInfo;
		this.gatewayConnectionService = gatewayConnectionService;
		this.gatewaySendMessageService = gatewaySendMessageService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 接続時に呼ばれるメソッド
		log.debug("メインWebSocket: 接続されました");
		session.setTextMessageSizeLimit(properties.getWebsocketMessageSizeLimit());
		
		// セッション更新
		// メッセージ送信スレッド起動
		gatewaySendMessageService.setSession(session);
		gatewaySendMessageService.exec();
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// テキストを受信したときに呼ばれるメソッド
		String payload = message.getPayload();
		discordMainService.receive(payload);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 切断時に呼ばれるメソッド
		log.debug("メインWebSocket: 切断されました");
		log.debug(status.getReason());

		if (isShuttingDown) {
			return;
		}

		// TODO ステータスコードを使用して再接続する
		// 再接続URL取得
		String connectUrl;
		if (gatewayInfo.getReconnectMode() == ReconnectMode.NORMAL) {
			connectUrl = gatewayInfo.getReadyDetail().getResumeGatewayUrl();
		} else {
			connectUrl = api.getGatewayUrl();
		}
		connectUrl += "/?v=10&encoding=json";

		// ディスコード再接続
		gatewayConnectionService.connect(connectUrl);
		log.info("再接続が完了しました");

	}

	@PreDestroy
	public void onShutdown() {
		log.debug("シャットダウンフラグを設定します");
		this.isShuttingDown = true;
	}
}
