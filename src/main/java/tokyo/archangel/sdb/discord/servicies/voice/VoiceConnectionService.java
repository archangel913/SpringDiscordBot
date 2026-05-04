package tokyo.archangel.sdb.discord.servicies.voice;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voiceserverupdate.VoiceServerUpdateDetail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code0.Code0Detail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.websocket.handler.VoiceWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class VoiceConnectionService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private VoiceWebSocketHandler discordVoiceWebSocketHandler;

	private SendMessageServiceProvider sendMessageServiceProvider;

	public VoiceConnectionService(VoiceWebSocketHandler discordVoiceWebSocketHandler,
			SendMessageServiceProvider sendMessageServiceProvider) {
		this.discordVoiceWebSocketHandler = discordVoiceWebSocketHandler;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
	}

	public void connect(VoiceServerUpdateDetail detail) {
		// websocket生成
		WebSocketClient client = new StandardWebSocketClient();

		log.debug("voiceWebsocket接続開始");
		client.execute(discordVoiceWebSocketHandler, "wss://" + detail.getEndpoint())
				.whenComplete((session, ex) -> {
					if (Objects.isNull(ex)) {
						log.info("voiceWebsocket接続完了");
						SendMessageService sendMessageService = sendMessageServiceProvider
								.generateSendMessageService(session, 0);
						sendMessageService.exec();
						sendIdentify(sendMessageService, detail);
					} else {
						log.error("voiceWebsocket接続失敗", ex);
					}
				});
	}

	private void sendIdentify(SendMessageService sendMessageService, VoiceServerUpdateDetail detail) {
		try {
			Code0Dto dto = new Code0Dto(new Code0Detail(detail.getGuildId(), null, null, detail.getToken(), 1));
			String json = objectMapper.writeValueAsString(dto);
			sendMessageService.sendMessage(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
