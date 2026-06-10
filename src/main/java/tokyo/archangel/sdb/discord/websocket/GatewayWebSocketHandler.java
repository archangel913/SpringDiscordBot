package tokyo.archangel.sdb.discord.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.api.DiscordApi;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.enumeration.GatewayWebsocketCode;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayConnectionService;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;

@Slf4j
public class GatewayWebSocketHandler extends TextWebSocketHandler {
	private static final String GATEWAY = "gateway";

	private GatewayService discordMainService;

	private DiscordApi api;

	private ApplicationProperties properties;

	private GatewayInfo gatewayInfo;

	private GatewayConnectionService gatewayConnectionService;

	private SendMessageServiceProvider sendMessageServiceProvider;

	private ApplicationContext context;

	private boolean isShuttingDown = false;

	public GatewayWebSocketHandler(GatewayService discordMainService, DiscordApi api, ApplicationProperties properties,
			GatewayInfo gatewayInfo, GatewayConnectionService gatewayConnectionService,
			SendMessageServiceProvider sendMessageServiceProvider,
			ApplicationContext context) {
		// TODO ロジックをサービス側に集約する
		this.discordMainService = discordMainService;
		this.api = api;
		this.properties = properties;
		this.gatewayInfo = gatewayInfo;
		this.gatewayConnectionService = gatewayConnectionService;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
		this.context = context;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 接続時に呼ばれるメソッド
		session.setTextMessageSizeLimit(properties.getWebsocketMessageSizeLimit());
		SendMessageService service = sendMessageServiceProvider.generateSendMessageService(session);
		sendMessageServiceProvider.setChannelId(session, GATEWAY);
		service.exec(GATEWAY);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// テキストを受信したときに呼ばれるメソッド
		String payload = message.getPayload();
		SendMessageService service = sendMessageServiceProvider.getServiceByChannelId(GATEWAY);
		discordMainService.receive(payload, service);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 切断時に呼ばれるメソッド
		log.debug("gatewayWebSocket: 切断されました");
		log.debug(String.valueOf(status.getCode()));
		log.debug(status.getReason());

		sendMessageServiceProvider.removeService(session);

		// シャットダウン中なら後続処理を行わない
		if (isShuttingDown) {
			return;
		}

		// ステータスコードによって再接続処理を切り替える
		GatewayWebsocketCode code = GatewayWebsocketCode.getGatewayWebsocketCode(status.getCode());
		if (code != GatewayWebsocketCode.NORMAL_CLOSURE && code != GatewayWebsocketCode.GOING_AWAY) {
			// 異常終了だった場合
			int failCount = gatewayInfo.getConnectionFailCount() + 1;
			if (failCount > 5) {
				// 既定回数以上失敗したらアプリケーションを落とす
				log.error("接続に5回連続で失敗しました。アプリケーションを終了します。");
				SpringApplication.exit(context, () -> 1);
				return;
			}
			gatewayInfo.setConnectionFailCount(failCount);

			// 再接続不可の場合、認証からやり直す
			if (!code.canReconnect()) {
				gatewayInfo.setReconnectMode(ReconnectMode.HARD);
			}
		}

		// 再接続URL取得
		String connectUrl;
		if (gatewayInfo.getReconnectMode() == ReconnectMode.NORMAL) {
			connectUrl = gatewayInfo.getReadyDetail().getResumeGatewayUrl();
		} else {
			connectUrl = api.getGatewayUrl();
		}
		connectUrl += "/?v=10&encoding=json";

		log.debug("再接続を行います");
		// ディスコード再接続
		gatewayConnectionService.connect(connectUrl);
	}

	@PreDestroy
	public void onShutdown() {
		log.debug("シャットダウンフラグを設定します");
		this.isShuttingDown = true;
	}
}
