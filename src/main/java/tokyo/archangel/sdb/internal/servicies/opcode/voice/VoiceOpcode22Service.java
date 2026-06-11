package tokyo.archangel.sdb.internal.servicies.opcode.voice;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code22.Code22Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code23.Code23Detail;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code23.Code23Dto;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class VoiceOpcode22Service implements VoiceOpcodeServiceInterface {

	private SendMessageService sendMessageService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのOPコード22を受信しました。");
		Code22Dto code22dto;
		if (dto instanceof Code22Dto) {
			code22dto = (Code22Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		// TODO opcode23は暗号化器の準備でき次第送信する形に変える
		Code23Dto code23dto = new Code23Dto(new Code23Detail(code22dto.getDetail().getTransitionId()));
		String json = objectMapper.writeValueAsString(code23dto);
		sendMessageService.sendMessage(json);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
