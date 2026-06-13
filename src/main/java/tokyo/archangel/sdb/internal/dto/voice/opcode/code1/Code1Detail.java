package tokyo.archangel.sdb.internal.dto.voice.opcode.code1;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code1Detail {
	@JsonProperty("protocol")
	private String protocol;

	@JsonProperty("data")
	private Data data;
}
