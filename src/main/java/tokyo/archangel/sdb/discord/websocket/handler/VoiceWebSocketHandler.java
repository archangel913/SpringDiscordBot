package tokyo.archangel.sdb.discord.websocket.handler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PreDestroy;
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
	
	private boolean isDisconennct = false;

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
		SendMessageService service = sendMessageServiceProvider.generateSendMessageService(session);
		discordVoiceService.receive(payload, service);
	}
	
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
	    SendMessageService service = sendMessageServiceProvider.generateSendMessageService(session);
	    discordVoiceService.recieve(message.getPayload(), service);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 切断時に呼ばれるメソッド
		log.debug("voiceWebSocket: 切断されました");
		log.debug(String.valueOf(status.getCode()));
		log.debug(status.getReason());
		
		// スレッドの終了処理が実行されているとは限らないので
		// 再度終了処理を呼んでおく
		SendMessageService service = sendMessageServiceProvider.generateSendMessageService(session);
		discordVoiceService.dispose(service);

		// 切断フラグが上がっていれば後続処理を行わない
		if (isDisconennct) {
			return;
		}
		
		// TODO ユーザーの操作で切られたりしたときは再接続を行わない
		// ステータスコードで判断

		// 再接続処理
		
		// TODO 再開に失敗した場合、再度新規接続を行う必要がある
		discordVoiceService.reconnect(service);
	}
	
	@PreDestroy
	public void onShutdown() {
		this.isDisconennct = true;
	}
}
