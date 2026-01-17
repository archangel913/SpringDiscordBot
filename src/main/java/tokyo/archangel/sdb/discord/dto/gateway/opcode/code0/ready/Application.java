package tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Application {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("flags")
	private Long flags;
}
