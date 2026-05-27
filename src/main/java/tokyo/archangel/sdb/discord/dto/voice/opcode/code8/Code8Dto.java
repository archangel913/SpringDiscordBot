package tokyo.archangel.sdb.discord.dto.voice.opcode.code8;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode8Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code8Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code8Detail detail;

	public Code8Dto(Code8Detail detail) {
		super(VoiceOpCode.HELLO);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode8Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
