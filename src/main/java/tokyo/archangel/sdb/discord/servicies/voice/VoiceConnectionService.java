package tokyo.archangel.sdb.discord.servicies.voice;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.websocket.handler.VoiceWebSocketHandler;

@Service
@Slf4j
public class VoiceConnectionService {
	private VoiceWebSocketHandler discordVoiceWebSocketHandler;

	public VoiceConnectionService(VoiceWebSocketHandler discordVoiceWebSocketHandler) {
		this.discordVoiceWebSocketHandler = discordVoiceWebSocketHandler;
	}

	public void connect(String url) {
		// websocket生成
		WebSocketClient client = new StandardWebSocketClient();

		log.debug("voiceWebsocket接続開始");
		client.execute(discordVoiceWebSocketHandler, url)
				.whenComplete((session, ex) -> {
					if (Objects.isNull(ex)) {
						log.info("voiceWebsocket接続完了");
					} else {
						log.error("voiceWebsocket接続失敗", ex);
					}
				});
	}
}
