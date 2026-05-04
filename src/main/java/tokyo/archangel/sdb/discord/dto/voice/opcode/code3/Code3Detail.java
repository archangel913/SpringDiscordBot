package tokyo.archangel.sdb.discord.dto.voice.opcode.code3;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code3Detail {
	@JsonProperty("t")
	private long nonce;
	
	@JsonProperty("seq_ack")
	private long seq_ack;
}
