package tokyo.archangel.sdb.internal.dto.gateway.opcode.code2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Presence {
	@JsonProperty("activities")
	private List<Activities> activities;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("since")
	private Long since;
	
	@JsonProperty("afk")
	private Boolean afk;
}
