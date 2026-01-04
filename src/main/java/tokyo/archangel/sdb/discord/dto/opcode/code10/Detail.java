package tokyo.archangel.sdb.discord.dto.opcode.code10;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Detail {
	@JsonProperty("heartbeat_interval")
	private Integer heartbeatInterval;
	@JsonProperty("_trace")
	private List<String> trace;
}
