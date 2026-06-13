package tokyo.archangel.sdb.internal.dto.voice.opcode.code7;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code7Detail {
	@JsonProperty("server_id")
	private String serverId;
	
	@JsonProperty("session_id")
	private String sessionId;
	
    @JsonProperty("token")
    private String token;
}
