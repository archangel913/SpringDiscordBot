package tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voiceserverupdate.VoiceServerUpdateDetail;
import tokyo.archangel.sdb.discord.servicies.opcode.OpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceConnectionService;

@Service
@Slf4j
public class VoiceServerUpdateEventService implements OpcodeServiceInterface {
	private VoiceConnectionService voiceConnectionService;

	public VoiceServerUpdateEventService(VoiceConnectionService voiceConnectionService) {
		this.voiceConnectionService = voiceConnectionService;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.info("VoiceServerUpdateイベントを受け取りました");
		VoiceServerUpdateDetail detail;
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof VoiceServerUpdateDetail) {
			detail = (VoiceServerUpdateDetail) ((Code0Dto) dto).getDetail();
		} else {
			log.warn("想定外の型のため処理を実行しません");
			return;
		}

		// TODO 音声接続処理実装
		// まずはハートビートから

		voiceConnectionService.connect("wss://" + detail.getEndpoint());

		// TODO Voice State Updateの処理

	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
	}
}
