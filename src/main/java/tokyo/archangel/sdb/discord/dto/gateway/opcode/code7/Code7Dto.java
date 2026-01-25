package tokyo.archangel.sdb.discord.dto.gateway.opcode.code7;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.Opcode7Service;

@Value
@EqualsAndHashCode(callSuper=true)
public class Code7Dto extends OpCodeBaseDto{
	@JsonProperty("d")
	private Object d;

	public Code7Dto(DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.RECONNECT, eventName, sequence);
		this.d = null;
	}
	
	@JsonIgnore
	@Override
	public String getServiceClassName(){
		String className = Opcode7Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
