package tokyo.archangel.sdb.discord.dto.voice.opcode.code23;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code23Detail {
	@JsonProperty("transition_id")
	private int transitionId;
}
