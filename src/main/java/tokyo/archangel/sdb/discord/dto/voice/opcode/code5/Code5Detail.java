package tokyo.archangel.sdb.discord.dto.voice.opcode.code5;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code5Detail {
	@JsonProperty("speaking")
	private Integer speaking;
	
	@JsonProperty("delay")
	private Integer delay;
	
	@JsonProperty("ssrc")
	private Integer ssrc;
}
