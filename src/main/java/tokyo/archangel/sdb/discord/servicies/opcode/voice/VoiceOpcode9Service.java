package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code9.Code9Dto;
import tokyo.archangel.sdb.discord.enumeration.ConnectingState;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Slf4j
public class VoiceOpcode9Service implements VoiceOpcodeServiceInterface {
	private SendMessageService sendMessageService;

	private VoiceChannels voiceChannels;

	public VoiceOpcode9Service(VoiceChannels voiceChannels) {
		this.voiceChannels = voiceChannels;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのopcode8を受信しました");
		Code9Dto code9dto;
		if (dto instanceof Code9Dto) {
			code9dto = (Code9Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		VoiceChannelInfo voiceInfo = voiceChannels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
		voiceInfo.setConnectingState(ConnectingState.CONNECTED);
		;
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

}
