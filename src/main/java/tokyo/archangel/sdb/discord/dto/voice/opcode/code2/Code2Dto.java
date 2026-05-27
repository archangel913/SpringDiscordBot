package tokyo.archangel.sdb.discord.dto.voice.opcode.code2;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode2Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code2Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code2Detail detail;

	public Code2Dto(Code2Detail detail) {
		super(VoiceOpCode.READY);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode2Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
