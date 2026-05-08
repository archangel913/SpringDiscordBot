package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code8.Code8Dto;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class Opcode8Service implements OpcodeServiceInterface {

	private SendMessageService sendMessageService;

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private VoiceChannels voiceChannels;

	public Opcode8Service(HeartBeatServiceProvider heartBeatServiceProvider, VoiceChannels voiceChannels) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.voiceChannels = voiceChannels;
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

		HeartBeatService heartBeatService = heartBeatServiceProvider
				.getHeartBeatService(sendMessageService.getSession());
		heartBeatService.setSendMessageService(sendMessageService);
		heartBeatService
				.setVoiceChannelInfo(voiceChannels.getVoiceChannelInfo(sendMessageService.getSession().getId()));
		heartBeatService.exec(code8dto.getDetail().getHeartbeatInterval());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

}
