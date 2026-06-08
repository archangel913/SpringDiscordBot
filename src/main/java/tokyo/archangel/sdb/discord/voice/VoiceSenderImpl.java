package tokyo.archangel.sdb.discord.voice;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.discord.enumeration.ConnectingState;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceSendService;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceSendServiceImpl;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceSessionProvider;
import tokyo.archangel.sdb.voice.VoiceSender;
import tools.jackson.databind.ObjectMapper;

/**
 * 
 */
@Service
@Scope("prototype")
@Slf4j
public class VoiceSenderImpl implements VoiceSender {
	private SendMessageServiceProvider sendMessageServiceProvider;
	
	private VoiceSessionProvider voiceSessionProvider;

	private VoiceChannels voiceChannels;

	private VoiceChannelInfo voiceInfo;

	private VoiceBinaryBuffer binaryBuffer;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final static String GATEWAY = "gateway";

	public VoiceSenderImpl(SendMessageServiceProvider sendMessageServiceProvider,
			VoiceSessionProvider voiceSessionProvider,
			VoiceChannels voiceChannels,
			VoiceBinaryBuffer binaryBuffer,
			VoiceSendServiceImpl sendThread) {
		this.voiceChannels = voiceChannels;
		this.binaryBuffer = binaryBuffer;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
		this.voiceSessionProvider = voiceSessionProvider;
	}

	@Override
	public void connect(String guildId, String channelId, boolean selfMute,
			boolean selfDeaf) {
		voiceInfo = voiceChannels.generateInfo(channelId);
		voiceInfo.setConnectingState(ConnectingState.CONNECTING);
		
		voiceInfo.setMute(selfMute);
		voiceInfo.setDeaf(selfDeaf);

		SendMessageService messageService = sendMessageServiceProvider.getServiceByChannelId(GATEWAY);
		Code4Dto dto = new Code4Dto(new Code4Detail(guildId, channelId, selfMute, selfDeaf));
		String json = objectMapper.writeValueAsString(dto);
		messageService.sendMessage(json);

		// 音声が送信可能になるまで待機
		voiceInfo.getReadyFuture().join();

		// 送信ループ実行
		voiceSessionProvider.getVoiceSendService(voiceInfo.getWebsocketGuid(), binaryBuffer, voiceInfo);
		voiceInfo.setConnectingState(ConnectingState.CONNECTED);
	}

	@Override
	public void disconnect() {
		// VoiceChannelInfoに切断フラグを持たせる
		if (voiceInfo == null) {
			log.warn("音声情報の取得に失敗しました。音声が接続されているか確認してください。");
			return;
		}

		VoiceSendService sendService = voiceSessionProvider.getVoiceSendService(voiceInfo.getWebsocketGuid());
		if(sendService == null) {
			log.warn("切断対象のサービスが見つかりません");
			return;
		}
		sendService.close();

		// UDP切断周りの処理はVoiceServiceに集約させる
		SendMessageService messageService = sendMessageServiceProvider.getServiceByChannelId(GATEWAY);
		Code4Dto dto = new Code4Dto(new Code4Detail(voiceInfo.getGuildId(), null, false, false));
		String json = objectMapper.writeValueAsString(dto);
		messageService.sendMessage(json);
	}

	@Override
	public void send(byte[] data) throws InterruptedException {
		// 実質このメソッドはバッファへバイナリを格納するだけのお仕事
		binaryBuffer.add(data);
	}
	
	@Override
	public void pause() {
		VoiceSendService sendService = voiceSessionProvider.getVoiceSendService(voiceInfo.getWebsocketGuid());
		if(sendService == null) {
			log.warn("操作対象のサービスが見つかりません");
			return;
		}
		sendService.pause();
	}
	
	@Override
	public void resume() {
		VoiceSendService sendService = voiceSessionProvider.getVoiceSendService(voiceInfo.getWebsocketGuid());
		if(sendService == null) {
			log.warn("操作対象のサービスが見つかりません");
			return;
		}
		sendService.resume();
	}

}
