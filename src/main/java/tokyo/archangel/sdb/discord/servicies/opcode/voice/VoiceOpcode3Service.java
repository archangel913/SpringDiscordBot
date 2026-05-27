package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
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
