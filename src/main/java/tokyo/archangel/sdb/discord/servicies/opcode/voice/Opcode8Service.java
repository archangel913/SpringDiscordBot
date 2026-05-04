package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class Opcode8Service implements OpcodeServiceInterface {
	
	private SendMessageService sendMessageService;
	
	private HeartBeatServiceProvider heartBeatServiceProvider;
	
	public Opcode8Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
	}
	
	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのopcode8を受信しました");
		/*
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
		heartBeatService.setSendOpcode(Code3Dto.class.getName());
		heartBeatService.exec(code8dto.getDetail().getHeartbeatInterval());
		*/
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

}
