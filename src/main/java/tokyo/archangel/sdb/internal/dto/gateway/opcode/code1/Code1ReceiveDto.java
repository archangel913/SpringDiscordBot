package tokyo.archangel.sdb.internal.dto.gateway.opcode.code1;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.TargetServiceNameObtainable;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.DispatchEvent;
import tokyo.archangel.sdb.internal.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcode1Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code1ReceiveDto extends OpCodeReceiveBaseDto implements TargetServiceNameObtainable {
	@JsonProperty("d")
	private Long d;

	public Code1ReceiveDto(Long d, DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.HELLO, eventName, sequence);
		this.d = d;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = GatewayOpcode1Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
