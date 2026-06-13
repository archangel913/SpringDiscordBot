package tokyo.archangel.sdb.internal.dto.voice.opcode.code23;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code23Detail {
	@JsonProperty("transition_id")
	private int transitionId;
}
