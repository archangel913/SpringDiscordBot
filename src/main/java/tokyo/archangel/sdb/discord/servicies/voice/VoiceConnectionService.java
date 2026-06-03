package tokyo.archangel.sdb.discord.servicies.voice;

import java.io.IOException;
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
import tokyo.archangel.sdb.discord.dto.voice.opcode.code7.Code7Detail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code7.Code7Dto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.websocket.handler.VoiceWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class VoiceConnectionService {
	// TODO 接続失敗時の処理が抜けてる

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
						SendMessageService sendMessageService = sendMessageServiceProvider
								.generateSendMessageService(session);
						sendMessageService.exec();

						VoiceChannelInfo info = channels.getInfoByGuildId(detail.getGuildId());
						info.setWebsocketGuid(session.getId());
						info.setEndpoint(detail.getEndpoint());
						info.setToken(detail.getToken());
						info.setGuildId(detail.getGuildId());
						if (canConnect(info)) {
							log.debug("Identifyを送信します");
							String json = generateIdentifyJson(sendMessageService, info);
							sendMessageService.sendMessage(json);
						} else {
							log.warn("接続に必要な情報がそろっていません。切断します");
							try {
								session.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else {
						log.error("voiceWebsocket接続失敗", ex);
					}
				}).join();
	}

	public void reconnect(VoiceChannelInfo info) {
		// websocket生成
		WebSocketClient client = new StandardWebSocketClient();

		log.debug("voiceWebsocket再接続開始");
		client.execute(discordVoiceWebSocketHandler, "wss://" + info.getEndpoint())
				.whenComplete((session, ex) -> {
					if (Objects.isNull(ex)) {
						log.info("voiceWebsocket接続完了");
						SendMessageService sendMessageService = sendMessageServiceProvider
								.generateSendMessageService(session);
						sendMessageService.exec();
						info.setOldWebsocketGuid(info.getWebsocketGuid());
						info.setWebsocketGuid(session.getId());

						if (canConnect(info)) {
							String json = generateReconnectJson(info);
							sendMessageService.sendMessage(json);
						} else {
							log.warn("接続に必要な情報がそろっていません。切断します。");
							try {
								session.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					} else {
						log.error("voiceWebsocket接続失敗", ex);
					}
				}).join();
	}

	private String generateReconnectJson(VoiceChannelInfo info) {
		Code7Dto dto = new Code7Dto(new Code7Detail(info.getGuildId(), info.getSessionId(), info.getToken()));
		return objectMapper.writeValueAsString(dto);
	}

	private String generateIdentifyJson(SendMessageService sendMessageService,
			VoiceChannelInfo info) {
		Code0Dto dto = new Code0Dto(
				new Code0Detail(info.getGuildId(), info.getUserId(), info.getSessionId(), info.getToken(), 1));
		return objectMapper.writeValueAsString(dto);
	}

	private boolean canConnect(VoiceChannelInfo info) {
		return info != null
				&& info.getGuildId() != null
				&& info.getUserId() != null
				&& info.getSessionId() != null
				&& info.getToken() != null;
	}
}
