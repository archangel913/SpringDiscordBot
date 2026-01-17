package tokyo.archangel.sdb.discord.dto.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Value;
import lombok.experimental.NonFinal;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code10.Code10Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code11.Code11Dto;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op", visible = true, defaultImpl = NotImplementCodeDto.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Code0Dto.class, name = "0"),
		@JsonSubTypes.Type(value = Code1Dto.class, name = "1"),
		/*
		@JsonSubTypes.Type(value = Bike.class, name = "2"),
		@JsonSubTypes.Type(value = Car.class, name = "3"),
		@JsonSubTypes.Type(value = Bike.class, name = "4"),
		@JsonSubTypes.Type(value = Car.class, name = "5"),
		@JsonSubTypes.Type(value = Bike.class, name = "6"),
		@JsonSubTypes.Type(value = Car.class, name = "7"),
		@JsonSubTypes.Type(value = Bike.class, name = "8"),
		@JsonSubTypes.Type(value = Car.class, name = "9"),
		*/
		@JsonSubTypes.Type(value = Code10Dto.class, name = "10"),
		@JsonSubTypes.Type(value = Code11Dto.class, name = "11")
})
@Value
@NonFinal
public abstract class OpCodeBaseDto implements ServiceClassNameInterface{
	@JsonProperty("op")
	private Integer opCode;
	@JsonProperty("t")
	private DispatchEvent eventName;
	@JsonProperty("s")
	private Long sequence;

	public OpCodeBaseDto(GatewayOpCode opCode, DispatchEvent eventName, Long sequence) {
		this.opCode = opCode.getValue();
		this.eventName = eventName;
		this.sequence = sequence;
	}
}
