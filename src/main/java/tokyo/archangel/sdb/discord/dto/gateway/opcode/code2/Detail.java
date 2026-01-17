package tokyo.archangel.sdb.discord.dto.gateway.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Detail {
	@JsonProperty("token")
	private String token;
	
	@JsonProperty("properties")
	private Properties properties;
	
	// ひとまず最低限
	/*
	@JsonProperty("compress")
	private Boolean compress;
	
	@JsonProperty("large_threshold")
	private Integer largeThreshold;
	
	@JsonProperty("shard")
	private List<Integer> shade;
	
	@JsonProperty("presence")
	private Presence presence;
	*/
	
	@JsonProperty("intents")
	private Integer intents;
	
}
