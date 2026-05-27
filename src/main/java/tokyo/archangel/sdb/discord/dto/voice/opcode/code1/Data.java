package tokyo.archangel.sdb.discord.dto.voice.opcode.code1;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Data {
	@JsonProperty("address")
	private String address;

	@JsonProperty("port")
	private Integer port;

	@JsonProperty("mode")
	private String mode;
}
