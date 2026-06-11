package tokyo.archangel.sdb.internal.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Value;
import lombok.experimental.NonFinal;
import tokyo.archangel.sdb.internal.dto.NotImplementCodeDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code23.Code23Dto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code3.Code3ReceiveDto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code5.Code5Dto;
import tokyo.archangel.sdb.internal.enumeration.VoiceOpCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op", visible = true, defaultImpl = NotImplementCodeDto.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Code0Dto.class, name = "0"),
		@JsonSubTypes.Type(value = Code1Dto.class, name = "1"),
		@JsonSubTypes.Type(value = Code3ReceiveDto.class, name = "3"),
		@JsonSubTypes.Type(value = Code5Dto.class, name = "5"),
		@JsonSubTypes.Type(value = Code23Dto.class, name = "23"),
})
@Value
@NonFinal
public abstract class OpCodeSendBaseDto {
	@JsonProperty("op")
	private Integer opCode;

	public OpCodeSendBaseDto(VoiceOpCode opCode) {
		this.opCode = opCode.getValue();
	}
}
