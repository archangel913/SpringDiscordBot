package tokyo.archangel.sdb.discord.dto.gateway.opcode.code1;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.ServiceClassNameInterface;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.Opcode1Service;

@Value
@EqualsAndHashCode(callSuper=true)
public class Code1Dto extends OpCodeBaseDto implements ServiceClassNameInterface{
	@JsonProperty("d")
	private Long d;

	public Code1Dto(Long d, DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.HEARTBEAT, eventName, sequence);
		this.d = d;
	}
	
	@JsonIgnore
	@Override
	public String getServiceClassName(){
		String className = Opcode1Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
