package tokyo.archangel.sdb.discord.servicies.opcode.gateway;

import java.util.Map;

import org.springframework.stereotype.Component;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Component
public class GatewayOpcodeServiceFactory {
	private Map<String, GatewayOpcodeServiceInterface> gatewayOpCodeServices;
	
	public GatewayOpcodeServiceFactory(Map<String, GatewayOpcodeServiceInterface> gatewayOpCodeServices) {
		this.gatewayOpCodeServices = gatewayOpCodeServices;
	}

	/**
	 * オペコードに応じたサービスクラスを生成します
	 * @param baseDto
	 * @param session
	 * @return
	 */
	public GatewayOpcodeServiceInterface create(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		GatewayOpcodeServiceInterface service = gatewayOpCodeServices.get(baseDto.getServiceClassName());
		service.setSendSessageService(sendMessageService);
		return service;
	}
}
