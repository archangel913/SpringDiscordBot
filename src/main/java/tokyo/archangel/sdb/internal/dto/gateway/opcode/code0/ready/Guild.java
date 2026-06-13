package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.ready;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Guild {
	@JsonProperty("unavailable")
	private Boolean unavailable;
	
	@JsonProperty("id")
	private String id;
}
