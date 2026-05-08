package tokyo.archangel.sdb.discord.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Value;
import lombok.experimental.NonFinal;
import tokyo.archangel.sdb.discord.dto.NotImplementCodeDto;
import tokyo.archangel.sdb.discord.dto.OpecodeServiceInitializeable;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code3.Code3ReceiveDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code8.Code8Dto;
import tokyo.archangel.sdb.discord.enumeration.VoiceOpCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op", visible = true, defaultImpl = NotImplementCodeDto.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Code2Dto.class, name = "2"),
		@JsonSubTypes.Type(value = Code3ReceiveDto.class, name = "3"),
		@JsonSubTypes.Type(value = Code8Dto.class, name = "8"),
})
@Value
@NonFinal
public abstract class OpCodeReceiveBaseDto implements OpecodeServiceInitializeable{
	@JsonProperty("op")
	private Integer opCode;

	public OpCodeReceiveBaseDto(VoiceOpCode opCode) {
		this.opCode = opCode.getValue();
	}
}
