package tokyo.archangel.sdb.internal.dto.gateway.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Activities {
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("type")
	private Integer type;
}
