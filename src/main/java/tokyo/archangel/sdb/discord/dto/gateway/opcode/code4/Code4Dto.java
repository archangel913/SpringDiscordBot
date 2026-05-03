package tokyo.archangel.sdb.discord.dto.gateway.opcode.code4;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code4Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code4Detail detail;

	public Code4Dto(Code4Detail detail) {
		super(GatewayOpCode.VOICE_STATE_UPDATE);
		this.detail = detail;
	}
}
