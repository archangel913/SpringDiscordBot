package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

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
	@SuppressWarnings("deprecation")
	public OpcodeServiceInterface create(OpCodeBaseDto baseDto, WebSocketSession session) {
		OpcodeServiceInterface service = gatewayOpCodeServices.get(((ServiceClassNameInterface)baseDto).getServiceClassName());
		if(service == null) {
			return null;
		}
		
		// 不用意にセッターを使わせないために専用のインターフェースへキャスト
		OpcodeSetterInterface init = (OpcodeSetterInterface) service;
		init.setSession(session);
		init.setDto(baseDto);
		
		return (OpcodeServiceInterface) service;
	}
}
