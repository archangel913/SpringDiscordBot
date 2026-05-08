package tokyo.archangel.sdb.discord.websocket.handler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceService;

@Component
@Scope("prototype")
@Slf4j
public class VoiceWebSocketHandler extends TextWebSocketHandler {
	private VoiceService discordVoiceService;

	private ApplicationProperties properties;

	private SendMessageServiceProvider sendMessageServiceProvider;

	public VoiceWebSocketHandler(VoiceService discordVoiceService, ApplicationProperties properties,
			SendMessageServiceProvider sendMessageServiceProvider) {
		this.discordVoiceService = discordVoiceService;
		this.properties = properties;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		session.setTextMessageSizeLimit(properties.getWebsocketMessageSizeLimit());
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// テキストを受信したときに呼ばれるメソッド
		String payload = message.getPayload();
		SendMessageService service = sendMessageServiceProvider.generateSendMessageService(session, 0);
		discordVoiceService.receive(payload, service);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO 1006 対策
		// Unexpected Status of SSLEngineResult after an unwrap() operation

		// 切断時に呼ばれるメソッド
		log.debug("voiceWebSocket: 切断されました");
		log.debug(String.valueOf(status.getCode()));
		log.debug(status.getReason());

		/*
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

		// ディスコード再接続
		voiceConnectionService.connect(connectUrl);
		log.info("再接続が完了しました");
		*/
	}
}
