package tokyo.archangel.sdb.discord.dto.voice.opcode.code22;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code22Detail {
	@JsonProperty("transition_id")
	private int transitionId;
}
