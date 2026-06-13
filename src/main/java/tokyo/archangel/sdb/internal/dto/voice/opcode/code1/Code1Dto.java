package tokyo.archangel.sdb.internal.dto.voice.opcode.code1;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code1Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code1Detail detail;

	public Code1Dto(Code1Detail detail) {
		super(VoiceOpCode.SELECT_PROTOCOL);
		this.detail = detail;
	}
}
