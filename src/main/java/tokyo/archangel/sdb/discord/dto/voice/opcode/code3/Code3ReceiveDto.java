package tokyo.archangel.sdb.discord.dto.voice.opcode.code3;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode3Service;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code3ReceiveDto extends OpCodeReceiveBaseDto {
	@JsonProperty("d")
	private Long detail;

	public Code3ReceiveDto(Long detail) {
		super(VoiceOpCode.HEARTBEAT);
		this.detail = detail;
	}
	
	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceOpcode3Service.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
