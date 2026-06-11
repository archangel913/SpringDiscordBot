package tokyo.archangel.sdb.internal.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Value;
import lombok.experimental.NonFinal;
import tokyo.archangel.sdb.internal.dto.NotImplementCodeDto;
import tokyo.archangel.sdb.internal.dto.TargetServiceNameObtainable;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code11.Code11Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code13.Code13Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code22.Code22Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code3.Code3ReceiveDto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code8.Code8Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code9.Code9Dto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op", visible = true, defaultImpl = NotImplementCodeDto.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Code2Dto.class, name = "2"),
		@JsonSubTypes.Type(value = Code3ReceiveDto.class, name = "3"),
		@JsonSubTypes.Type(value = Code4Dto.class, name = "4"),
		@JsonSubTypes.Type(value = Code8Dto.class, name = "8"),
		@JsonSubTypes.Type(value = Code9Dto.class, name = "9"),
		@JsonSubTypes.Type(value = Code11Dto.class, name = "11"),
		@JsonSubTypes.Type(value = Code13Dto.class, name = "13"),
		@JsonSubTypes.Type(value = Code22Dto.class, name = "22"),
})
@Value
@NonFinal
public abstract class OpCodeReceiveBaseDto implements TargetServiceNameObtainable{
	@JsonProperty("op")
	private Integer opCode;

	public OpCodeReceiveBaseDto(VoiceOpCode opCode) {
		this.opCode = opCode.getValue();
	}
}
