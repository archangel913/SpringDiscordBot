package tokyo.archangel.sdb.internal.dto.gateway.opcode.code10;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code10Detail {
	@JsonProperty("heartbeat_interval")
	private Integer heartbeatInterval;
	@JsonProperty("_trace")
	private List<String> trace;
}
