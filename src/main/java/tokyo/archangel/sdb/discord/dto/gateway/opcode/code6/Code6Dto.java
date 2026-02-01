package tokyo.archangel.sdb.discord.dto.gateway.opcode.code6;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@Value
@EqualsAndHashCode(callSuper=true)
public class Code6Dto extends OpCodeSendBaseDto{
	@JsonProperty("d")
	private Code6Detail detail;

	public Code6Dto(Code6Detail detail) {
		super(GatewayOpCode.RESUME);
		this.detail = detail;
	}
}
