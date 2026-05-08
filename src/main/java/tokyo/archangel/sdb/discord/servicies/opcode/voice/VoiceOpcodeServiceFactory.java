package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import java.util.Map;

import org.springframework.stereotype.Component;

import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Component
public class VoiceOpcodeServiceFactory {
	private Map<String, OpcodeServiceInterface> voiceOpCodeServices;
	
	public VoiceOpcodeServiceFactory(Map<String, OpcodeServiceInterface> voiceOpCodeServices) {
		this.voiceOpCodeServices = voiceOpCodeServices;
	}

	/**
	 * オペコードに応じたサービスクラスを生成します
	 * @param baseDto
	 * @param session
	 * @return
	 */
	public OpcodeServiceInterface create(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		OpcodeServiceInterface service = voiceOpCodeServices.get(baseDto.getServiceClassName());
		service.setSendSessageService(sendMessageService);
		return service;
	}
}
