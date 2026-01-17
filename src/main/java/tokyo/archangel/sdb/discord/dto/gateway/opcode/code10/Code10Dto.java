package tokyo.archangel.sdb.discord.dto.gateway.opcode.code10;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.ServiceClassNameInterface;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.Opcode10Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code10Dto extends OpCodeBaseDto implements ServiceClassNameInterface{
	@JsonProperty("d")
	private Detail detail;

	public Code10Dto(Detail detail, DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.HELLO, eventName, sequence);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = Opcode10Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
