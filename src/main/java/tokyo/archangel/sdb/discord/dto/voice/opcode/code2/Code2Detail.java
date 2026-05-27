package tokyo.archangel.sdb.discord.dto.voice.opcode.code2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code2Detail {
	@JsonProperty("streams")
	private List<Streams> streams;
	
	@JsonProperty("ssrc")
	private Integer ssrc;
	
	@JsonProperty("port")
	private Integer port;
	
	@JsonProperty("modes")
	private List<String> modes;
	
	@JsonProperty("ip")
	private String ip;
	
	@JsonProperty("heartbeat_interval")
	private Integer heartbeatInterval;
	
	@JsonProperty("experiments")
	private List<String> experiments;
}
