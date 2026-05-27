package tokyo.archangel.sdb.discord.voice;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.voice.VoiceSender;
import tools.jackson.databind.ObjectMapper;

/**
 * 
 */
@Service
@Scope("prototype")
public class VoiceSenderImpl implements VoiceSender {
	private SendMessageServiceProvider sendMessageServiceProvider;

	private VoiceChannels voiceChannels;

	private VoiceChannelInfo voiceInfo;

	private VoiceBinaryBuffer binaryBuffer;

	private VoiceSendThread sendThread;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public VoiceSenderImpl(SendMessageServiceProvider sendMessageServiceProvider,
			VoiceChannels voiceChannels,
			VoiceBinaryBuffer binaryBuffer,
			VoiceSendThread sendThread) {
		this.voiceChannels = voiceChannels;
		this.binaryBuffer = binaryBuffer;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
		this.sendThread = sendThread;
	}

	@Override
	public void connectAsync(String guildId, String channelId, boolean selfMute, boolean selfDeaf) {
		voiceInfo = voiceChannels.generateInfo(channelId);

		SendMessageService messageService = sendMessageServiceProvider.getServiceByChannelId("gateway");
		Code4Dto dto = new Code4Dto(new Code4Detail(guildId, channelId, selfMute, selfDeaf));
		String json = objectMapper.writeValueAsString(dto);
		messageService.sendMessage(json);

		// 音声が送信可能になるまで待機
		// タイムアウト10秒
		// voiceInfo.getReadyFuture().orTimeout(10, TimeUnit.SECONDS).join();
		
		voiceInfo.getReadyFuture().join();

		// 送信ループ実行
		sendThread.init(binaryBuffer, voiceInfo);
		sendThread.send();
	}

	@Override
	public void disconnectAsync() {
		// TODO 切断処理実装
	}

	@Override
	public void sendAsync(byte[] data) throws InterruptedException {
		// 実質このメソッドはバッファへバイナリを格納するだけのお仕事
		binaryBuffer.add(data);
	}
}
