package tokyo.archangel.sdb.discord.dto.voice.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code2Detail {
	@JsonProperty("heartbeat_interval")
	private Integer heartbeatInterval;
}
