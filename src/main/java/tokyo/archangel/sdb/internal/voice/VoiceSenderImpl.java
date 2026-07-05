package tokyo.archangel.sdb.internal.voice;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannels;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.internal.enumeration.ConnectingState;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceResourceProvider;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceSendService;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceSendServiceImpl;
import tokyo.archangel.sdb.voice.VoiceSender;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class VoiceSenderImpl implements VoiceSender {
	private SendMessageServiceProvider sendMessageServiceProvider;

	private VoiceResourceProvider voiceSessionProvider;

	private VoiceChannels voiceChannels;

	private VoiceChannelInfo voiceInfo;

	private VoiceBinaryBuffer binaryBuffer;

	private boolean isMute = false;

	private boolean isDeaf = false;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final static String GATEWAY = "gateway";

	public VoiceSenderImpl(SendMessageServiceProvider sendMessageServiceProvider,
			VoiceResourceProvider voiceSessionProvider,
			VoiceChannels voiceChannels,
			VoiceBinaryBuffer binaryBuffer,
			VoiceSendServiceImpl sendThread) {
		this.voiceChannels = voiceChannels;
		this.binaryBuffer = binaryBuffer;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
		this.voiceSessionProvider = voiceSessionProvider;
	}

	@Override
	public void connect(String guildId, String channelId) {
		voiceInfo = voiceChannels.generateInfo(channelId);
		voiceInfo.setConnectingState(ConnectingState.CONNECTING);

		voiceInfo.setMute(isMute);
		voiceInfo.setDeaf(isDeaf);

		// TODO 途中でミュート状態が変わったときの対応
		SendMessageService messageService = sendMessageServiceProvider.getServiceByChannelId(GATEWAY);
		Code4Dto dto = new Code4Dto(new Code4Detail(guildId, channelId, isMute, isDeaf));
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
		if (sendService == null) {
			log.warn("切断対象のサービスが見つかりません");
			return;
		}

		// バッファのクリア
		binaryBuffer.clear();

		// UDP切断周りの処理はVoiceServiceに集約させる
		SendMessageService messageService = sendMessageServiceProvider.getServiceByChannelId(GATEWAY);
		Code4Dto dto = new Code4Dto(new Code4Detail(voiceInfo.getGuildId(), null, isMute, isDeaf));
		String json = objectMapper.writeValueAsString(dto);
		messageService.sendMessage(json);
	}

	@Override
	public void send(byte[] data) throws InterruptedException {
		// 実質このメソッドはバッファへバイナリを格納するだけのお仕事
		binaryBuffer.add(data);
	}

	@Override
	public void clearBuffer() {
		binaryBuffer.clear();
	}

	@Override
	public void pause() {
		VoiceSendService sendService = voiceSessionProvider.getVoiceSendService(voiceInfo.getWebsocketGuid());
		if (sendService == null) {
			log.warn("操作対象のサービスが見つかりません");
			return;
		}
		sendService.pause();
	}

	@Override
	public void resume() {
		VoiceSendService sendService = voiceSessionProvider.getVoiceSendService(voiceInfo.getWebsocketGuid());
		if (sendService == null) {
			log.warn("操作対象のサービスが見つかりません");
			return;
		}
		sendService.resume();
	}

	@Override
	public void setMute(boolean isMute) {
		this.isMute = isMute;
	}

	public void setDeaf(boolean isDeaf) {
		this.isDeaf = isDeaf;
	}

}
