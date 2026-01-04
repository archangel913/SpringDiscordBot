package tokyo.archangel.sdb.discord.dto.opcode.code1;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.opcode.OpCodeBaseDto;

@Value
@EqualsAndHashCode(callSuper=true)
public class Code1Dto extends OpCodeBaseDto {
	@JsonProperty("d")
	private Integer d;

	public Code1Dto(Integer d) {
		super(1);
		this.d = d;
	}
}
