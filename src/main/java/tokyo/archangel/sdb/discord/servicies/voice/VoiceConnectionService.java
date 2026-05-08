package tokyo.archangel.sdb.discord.servicies.voice;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
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

	private final static int MAX_RETRY_COUNT = 5;
	
	private final static int SLEEP_BASE_TIME = 100;

	private VoiceWebSocketHandler discordVoiceWebSocketHandler;

	private SendMessageServiceProvider sendMessageServiceProvider;

	private VoiceChannels channels;

	public VoiceConnectionService(VoiceWebSocketHandler discordVoiceWebSocketHandler,
			SendMessageServiceProvider sendMessageServiceProvider,
			VoiceChannels channels) {
		this.discordVoiceWebSocketHandler = discordVoiceWebSocketHandler;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
		this.channels = channels;
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
			VoiceChannelInfo info = channels.getVoiceChannelInfo(sendMessageService.getSession().getId());
			int sleepTime = SLEEP_BASE_TIME;
			for (int i = 0; i < MAX_RETRY_COUNT; ++i) {
				if (canConnect(info, detail)) {
					Code0Dto dto = new Code0Dto(
							new Code0Detail(detail.getGuildId(), info.getUserId(), info.getSessionId(),
									detail.getToken(), 1));
					String json = objectMapper.writeValueAsString(dto);
					sendMessageService.sendMessage(json);
					break;
				} else {
					log.warn("接続に必要な情報がそろっていません。再度リトライします。");
					Thread.sleep(sleepTime);
					sleepTime *= 2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean canConnect(VoiceChannelInfo info, VoiceServerUpdateDetail detail) {
		return info != null
				&& detail != null
				&& detail.getGuildId() != null
				&& info.getUserId() != null
				&& info.getSessionId() != null
				&& detail.getToken() != null;
	}
}
