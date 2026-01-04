package tokyo.archangel.sdb.discord.dto.opcode.code10;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.opcode.OpCodeBaseDto;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code10Dto extends OpCodeBaseDto {
	@JsonProperty("t")
	private String t;
	@JsonProperty("s")
	private String s;
	@JsonProperty("d")
	private Detail detail;

	public Code10Dto(String t, String s, Detail detail) {
		super(10);
		this.t = t;
		this.s = s;
		this.detail = detail;
	}
}
