package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import java.util.Map;

import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

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
		if (service == null) {
			return null;
		}
		service.setSendSessageService(sendMessageService);
		return service;
	}
}
