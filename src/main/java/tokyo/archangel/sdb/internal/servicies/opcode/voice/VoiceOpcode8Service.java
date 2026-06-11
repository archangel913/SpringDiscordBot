package tokyo.archangel.sdb.internal.servicies.opcode.voice;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannels;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code8.Code8Dto;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageServiceProvider;

@Slf4j
public class VoiceOpcode8Service implements VoiceOpcodeServiceInterface {

	private SendMessageServiceProvider messageServiceProvider;

	private SendMessageService sendMessageService;

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private VoiceChannels voiceChannels;

	public VoiceOpcode8Service(HeartBeatServiceProvider heartBeatServiceProvider, VoiceChannels voiceChannels,
			SendMessageServiceProvider messageServiceProvider) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.voiceChannels = voiceChannels;
		this.messageServiceProvider = messageServiceProvider;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのopcode8を受信しました");
		Code8Dto code8dto;
		if (dto instanceof Code8Dto) {
			code8dto = (Code8Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		VoiceChannelInfo voiceInfo = voiceChannels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
		voiceInfo.setHeartBeatInterval(code8dto.getDetail().getHeartbeatInterval());
		messageServiceProvider.setChannelId(sendMessageService.getSession(), voiceInfo.getChannelId());

		HeartBeatService heartBeatService = heartBeatServiceProvider
				.getHeartBeatService(sendMessageService.getSession());
		heartBeatService.setSendMessageService(sendMessageService);
		heartBeatService.setVoiceChannelInfo(voiceInfo);
		heartBeatService.exec(voiceInfo.getHeartBeatInterval(), voiceInfo.getChannelId());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

}
