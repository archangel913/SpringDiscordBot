package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voiceserverupdate;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch.VoiceServerUpdateEventService;

@Value
@EqualsAndHashCode(callSuper = true)
public class VoiceServerUpdateDetail extends EventDetailBase  {

	@JsonProperty("token")
	private String token;
	
	@JsonProperty("guild_id")
	private String guildId;
	
	@JsonProperty("endpoint")
	private String endpoint;
	
	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceServerUpdateEventService.class.getSimpleName();
		return Introspector.decapitalize(className);
	}

}
