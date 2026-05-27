package tokyo.archangel.sdb.discord.dto.voice.opcode.code7;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code7Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code7Detail detail;

	public Code7Dto(Code7Detail detail) {
		super(VoiceOpCode.RESUME);
		this.detail = detail;
	}
}
