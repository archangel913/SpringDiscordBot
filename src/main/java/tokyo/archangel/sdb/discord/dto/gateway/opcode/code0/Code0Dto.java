package tokyo.archangel.sdb.discord.dto.gateway.opcode.code0;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.NotImplementCodeDto;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.OpecodeServiceInitializeable;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voiceserverupdate.VoiceServerUpdateDetail;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code0Dto extends OpCodeReceiveBaseDto {

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "t", visible = true, defaultImpl = NotImplementCodeDto.class)
	@JsonSubTypes({
			@JsonSubTypes.Type(value = ReadyDetail.class, name = "READY"),
			@JsonSubTypes.Type(value = VoiceServerUpdateDetail.class, name = "VOICE_SERVER_UPDATE"),
	})
	@JsonProperty("d")
	private EventDetailBase detail;

	public Code0Dto(
			@JsonProperty("t") DispatchEvent event,
			@JsonProperty("d") EventDetailBase detail,
			@JsonProperty("s") Long sequence) {
		super(GatewayOpCode.DISPATCH, event, sequence);
		this.detail = detail;
	}

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		return ((OpecodeServiceInitializeable) detail).getServiceClassName();
	}

}
