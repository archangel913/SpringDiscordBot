package tokyo.archangel.sdb.internal.dto.voice.opcode.code22;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.voice.VoiceOpcode22Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code22Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code22Detail detail;

	public Code22Dto(Code22Detail detail) {
		super(VoiceOpCode.DAVE_PROTOCOL_EXECUTE_TRANSITION);
		this.detail = detail;
	}

	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode22Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
