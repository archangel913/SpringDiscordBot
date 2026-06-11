package tokyo.archangel.sdb.internal.dto.voice.opcode.code8;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code8Detail {
	@JsonProperty("heartbeat_interval")
	private Integer heartbeatInterval;
}
