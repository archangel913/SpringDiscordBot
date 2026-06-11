package tokyo.archangel.sdb.internal.servicies.opcode.voice;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

@Slf4j
public class VoiceOpcode3Service implements VoiceOpcodeServiceInterface {

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private SendMessageService sendMessageService;

	public VoiceOpcode3Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voice: ハートビートを確認しました");
		HeartBeatService service = heartBeatServiceProvider.getHeartBeatService(sendMessageService.getSession());
		service.receiveAck();
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
