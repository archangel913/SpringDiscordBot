package tokyo.archangel.sdb.discord.dto.voice.opcode.code3;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code3Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code3Detail detail;

	public Code3Dto(Code3Detail detail) {
		super(VoiceOpCode.HEARTBEAT);
		this.detail = detail;
	}
}
