package tokyo.archangel.sdb.discord.dto.gateway.opcode.code6;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code6Detail {
	@JsonProperty("token")
	private String token;

	@JsonProperty("session_id")
	private String sessionId;

	@JsonProperty("seq")
	private Long sequence;
}
