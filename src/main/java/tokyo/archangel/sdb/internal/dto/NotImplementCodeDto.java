package tokyo.archangel.sdb.internal.dto;

import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.DispatchEvent;
import tokyo.archangel.sdb.internal.enumeration.GatewayOpCode;

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
