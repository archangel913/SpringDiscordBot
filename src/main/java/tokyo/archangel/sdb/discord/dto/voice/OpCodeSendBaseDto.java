package tokyo.archangel.sdb.discord.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Value;
import lombok.experimental.NonFinal;
import tokyo.archangel.sdb.discord.dto.NotImplementCodeDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code3.Code3Dto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op", visible = true, defaultImpl = NotImplementCodeDto.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Code0Dto.class, name = "0"),
		@JsonSubTypes.Type(value = Code3Dto.class, name = "3"),
})
@Value
@NonFinal
public abstract class OpCodeSendBaseDto{
	@JsonProperty("op")
	private Integer opCode;

	public OpCodeSendBaseDto(VoiceOpCode opCode) {
		this.opCode = opCode.getValue();
	}
}
