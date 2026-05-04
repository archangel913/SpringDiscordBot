package tokyo.archangel.sdb.discord.dto;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

public class NotImplementCodeDto extends OpCodeReceiveBaseDto{

	public NotImplementCodeDto(GatewayOpCode opCode, DispatchEvent eventName, Long sequence) {
		super(opCode, eventName, sequence);
		// モック
	}

	@Override
	public String getServiceClassName() {
		// モック
		return null;
	}

}
