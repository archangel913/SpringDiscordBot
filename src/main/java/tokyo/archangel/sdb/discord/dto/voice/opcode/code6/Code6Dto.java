package tokyo.archangel.sdb.discord.dto.voice.opcode.code6;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.Opcode6Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code6Dto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Code6Detail detail;

	public Code6Dto(Code6Detail detail) {
		super(VoiceOpCode.HEARTBEAT_ACK);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = Opcode6Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
