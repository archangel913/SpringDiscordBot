package tokyo.archangel.sdb.internal.dto.gateway.opcode.code9;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.DispatchEvent;
import tokyo.archangel.sdb.internal.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcode9Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code9Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Boolean d;

	public Code9Dto(Boolean d, DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.INVALID_SESSION, eventName, sequence);
		this.d = d;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = GatewayOpcode9Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
