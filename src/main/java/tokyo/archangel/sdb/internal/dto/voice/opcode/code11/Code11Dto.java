package tokyo.archangel.sdb.internal.dto.voice.opcode.code11;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.internal.servicies.opcode.voice.VoiceOpcode11Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code11Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code11Detail detail;

	public Code11Dto(Code11Detail detail) {
		super(VoiceOpCode.CLIENTS_CONNECT);
		this.detail = detail;
	}

	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode11Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
