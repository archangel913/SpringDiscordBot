package tokyo.archangel.sdb.discord.dto.gateway.opcode.code11;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.ServiceClassNameInterface;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.Opcode11Service;

@EqualsAndHashCode(callSuper = true)
public class Code11Dto extends OpCodeReceiveBaseDto implements ServiceClassNameInterface{
	public Code11Dto(DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.HEARTBEAT_ACK, eventName, sequence);
	}
	
	@JsonIgnore
	@Override
	public String getServiceClassName(){
		String className = Opcode11Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
