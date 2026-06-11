package tokyo.archangel.sdb.internal.servicies.opcode.voice;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannels;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code11.Code11Dto;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

@Slf4j
public class VoiceOpcode11Service implements VoiceOpcodeServiceInterface {

	private SendMessageService sendMessageService;

	VoiceChannels channels;

	public VoiceOpcode11Service(VoiceChannels channels) {
		this.channels = channels;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのOPコード11を受信しました。");
		Code11Dto code11dto;
		if (dto instanceof Code11Dto) {
			code11dto = (Code11Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		VoiceChannelInfo info = channels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
		info.getJoinedUserIds().addAll(code11dto.getDetail().getUserIds());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
