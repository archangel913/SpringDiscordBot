package tokyo.archangel.sdb.discord.dto.gateway.opcode.code1;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@Value
@EqualsAndHashCode(callSuper=true)
public class Code1SendDto extends OpCodeSendBaseDto{
	@JsonProperty("d")
	private Long d;

	public Code1SendDto(Long d) {
		super(GatewayOpCode.HEARTBEAT);
		this.d = d;
	}
}
