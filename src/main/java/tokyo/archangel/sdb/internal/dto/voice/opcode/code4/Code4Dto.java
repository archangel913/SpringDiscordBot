package tokyo.archangel.sdb.internal.dto.voice.opcode.code4;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.voice.VoiceOpcode4Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code4Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code4Detail detail;

	public Code4Dto(Code4Detail detail) {
		super(VoiceOpCode.SESSION_DISCRIPTION);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode4Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
