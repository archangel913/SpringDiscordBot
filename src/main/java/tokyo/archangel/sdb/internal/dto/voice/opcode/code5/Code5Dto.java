package tokyo.archangel.sdb.internal.dto.voice.opcode.code5;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code5Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code5Detail detail;

	public Code5Dto(Code5Detail detail) {
		super(VoiceOpCode.SPEAKING);
		this.detail = detail;
	}
}
