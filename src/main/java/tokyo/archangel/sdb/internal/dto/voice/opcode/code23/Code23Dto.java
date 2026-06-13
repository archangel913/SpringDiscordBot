package tokyo.archangel.sdb.internal.dto.voice.opcode.code23;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code23Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code23Detail detail;

	public Code23Dto(Code23Detail detail) {
		super(VoiceOpCode.DAVE_PROTOCOL_READY_FOR_TRANSITION);
		this.detail = detail;
	}
}
