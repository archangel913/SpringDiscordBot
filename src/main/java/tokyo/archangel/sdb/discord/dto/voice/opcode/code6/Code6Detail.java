package tokyo.archangel.sdb.discord.dto.voice.opcode.code6;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code6Detail {
	@JsonProperty("t")
	private Long nonce;
}
