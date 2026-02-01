package tokyo.archangel.sdb.discord.dto.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Value;
import lombok.experimental.NonFinal;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code6.Code6Dto;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op", visible = true, defaultImpl = NotImplementCodeDto.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Code1SendDto.class, name = "1"),
		@JsonSubTypes.Type(value = Code2Dto.class, name = "2"),
		@JsonSubTypes.Type(value = Code6Dto.class, name = "6")
})
@Value
@NonFinal
public abstract class OpCodeSendBaseDto{
	@JsonProperty("op")
	private Integer opCode;

	public OpCodeSendBaseDto(GatewayOpCode opCode) {
		this.opCode = opCode.getValue();
	}
}
