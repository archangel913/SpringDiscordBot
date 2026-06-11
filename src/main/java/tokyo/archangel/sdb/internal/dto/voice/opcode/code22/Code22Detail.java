package tokyo.archangel.sdb.internal.dto.voice.opcode.code22;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code22Detail {
	@JsonProperty("transition_id")
	private int transitionId;
}
