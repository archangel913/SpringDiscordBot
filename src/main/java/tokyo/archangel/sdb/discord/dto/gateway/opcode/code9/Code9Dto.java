package tokyo.archangel.sdb.discord.dto.gateway.opcode.code9;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.ServiceClassNameInterface;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.Opcode9Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code9Dto extends OpCodeReceiveBaseDto implements ServiceClassNameInterface {
	@JsonProperty("d")
	private Boolean d;

	public Code9Dto(Boolean d, DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.INVALID_SESSION, eventName, sequence);
		this.d = d;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = Opcode9Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
