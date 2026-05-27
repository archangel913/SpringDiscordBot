package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code13.Code13Dto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class VoiceOpcode13Service implements VoiceOpcodeServiceInterface {

	private SendMessageService sendMessageService;
	
	VoiceChannels channels;
	
	public VoiceOpcode13Service(VoiceChannels channels) {
		this.channels = channels;
	}
	
	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのOPコード13を受信しました。");
		Code13Dto code13dto;
		if (dto instanceof Code13Dto) {
			code13dto = (Code13Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}
		
		VoiceChannelInfo info = channels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
		info.getJoinedUserIds().removeAll(code13dto.getDetail().getUserIds());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
