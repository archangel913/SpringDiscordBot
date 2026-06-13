package tokyo.archangel.sdb.internal.dto.gateway.opcode.code11;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.DispatchEvent;
import tokyo.archangel.sdb.internal.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcode11Service;


@EqualsAndHashCode(callSuper = true)
public class Code11Dto extends OpCodeReceiveBaseDto{
	public Code11Dto(DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.HEARTBEAT_ACK, eventName, sequence);
	}
	
	@JsonIgnore
	@Override
	public String getServiceClassName(){
		String className = GatewayOpcode11Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
