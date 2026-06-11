package tokyo.archangel.sdb.internal.dto.voice.opcode.code7;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;

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
