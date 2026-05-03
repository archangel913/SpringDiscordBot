package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import java.util.Map;

import org.springframework.stereotype.Component;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.opcode.OpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Component
public class VoiceOpcodeServiceFactory {
	private Map<String, OpcodeServiceInterface> gatewayOpCodeServices;
	
	public VoiceOpcodeServiceFactory(Map<String, OpcodeServiceInterface> gatewayOpCodeServices) {
		this.gatewayOpCodeServices = gatewayOpCodeServices;
	}

	/**
	 * オペコードに応じたサービスクラスを生成します
	 * @param baseDto
	 * @param session
	 * @return
	 */
	public OpcodeServiceInterface create(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		OpcodeServiceInterface service = gatewayOpCodeServices.get(baseDto.getServiceClassName());
		service.setSendSessageService(sendMessageService);
		return service;
	}
}
