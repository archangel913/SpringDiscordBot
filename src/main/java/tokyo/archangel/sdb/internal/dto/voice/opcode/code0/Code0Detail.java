package tokyo.archangel.sdb.internal.dto.voice.opcode.code0;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code0Detail {
	@JsonProperty("server_id")
	private String serverId; 
	
	@JsonProperty("user_id")
	private String userId; 
	
	@JsonProperty("session_id")
	private String sessionId; 
	
	@JsonProperty("token")
	private String token; 
	
	@JsonProperty("max_dave_protocol_version")
	private int maxDaveProtocolVersion;
}
