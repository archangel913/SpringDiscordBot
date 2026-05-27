package tokyo.archangel.sdb.discord.dto.voice.opcode.code13;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode13Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code13Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code13Detail detail;

	public Code13Dto(Code13Detail detail) {
		super(VoiceOpCode.CLIENTS_DISCONNECT);
		this.detail = detail;
	}

	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode13Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
