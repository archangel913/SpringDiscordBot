package tokyo.archangel.sdb.internal.dto.gateway.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeSendBaseDto;
import tokyo.archangel.sdb.internal.enumeration.GatewayOpCode;

@Value
@EqualsAndHashCode(callSuper=true)
public class Code2Dto extends OpCodeSendBaseDto{
	@JsonProperty("d")
	private Code2Detail detail;

	public Code2Dto(Code2Detail detail) {
		super(GatewayOpCode.IDENTIFY);
		this.detail = detail;
	}
}
