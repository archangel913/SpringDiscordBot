package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.ServiceClassNameInterface;

@Component
public class OpcodeServiceFactory {
	@Autowired
	private Map<String, OpcodeServiceInterface> gatewayOpCodeServices;
	
	public OpcodeServiceFactory(Map<String, OpcodeServiceInterface> gatewayOpCodeServices) {
		this.gatewayOpCodeServices = gatewayOpCodeServices;
	}

	/**
	 * オペコードに応じたサービスクラスを生成します
	 * @param baseDto
	 * @param session
	 * @return
	 */
	public OpcodeServiceInterface create(OpCodeBaseDto baseDto) {
		return gatewayOpCodeServices.get(((ServiceClassNameInterface)baseDto).getServiceClassName());
	}
}
