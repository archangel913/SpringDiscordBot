package tokyo.archangel.sdb.internal.dto.voice.opcode.code9;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.voice.VoiceOpcode9Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code9Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code9Detail detail;

	public Code9Dto(Code9Detail detail) {
		super(VoiceOpCode.RESUMED);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode9Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
