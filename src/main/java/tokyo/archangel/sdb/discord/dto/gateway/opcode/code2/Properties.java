package tokyo.archangel.sdb.discord.dto.gateway.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Properties {
	@JsonProperty("os")
	private String os;
	
	@JsonProperty("browser")
	private String browser;
	
	@JsonProperty("device")
	private String device;
}
