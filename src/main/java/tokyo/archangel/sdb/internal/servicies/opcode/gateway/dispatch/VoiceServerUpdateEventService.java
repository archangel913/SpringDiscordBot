package tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voiceserverupdate.VoiceServerUpdateDetail;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceConnectionService;

@Slf4j
public class VoiceServerUpdateEventService implements GatewayOpcodeServiceInterface {
	private VoiceConnectionService voiceConnectionService;

	public VoiceServerUpdateEventService(VoiceConnectionService voiceConnectionService) {
		this.voiceConnectionService = voiceConnectionService;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("VoiceServerUpdateイベントを受け取りました");
		VoiceServerUpdateDetail detail;
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof VoiceServerUpdateDetail) {
			detail = (VoiceServerUpdateDetail) ((Code0Dto) dto).getDetail();
		} else {
			log.warn("想定外の型のため処理を実行しません");
			return;
		}

		// TODO 接続失敗時のために履行ロジックを組む
		voiceConnectionService.connect(detail);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
	}
}
