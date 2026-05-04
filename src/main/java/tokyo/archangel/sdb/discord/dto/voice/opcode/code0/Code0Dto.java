package tokyo.archangel.sdb.discord.dto.voice.opcode.code0;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code0Dto extends OpCodeSendBaseDto {
	@JsonProperty("d")
	private Code0Detail detail;

	public Code0Dto(Code0Detail detail) {
		super(VoiceOpCode.IDENTIFY);
		this.detail = detail;
	}
}
