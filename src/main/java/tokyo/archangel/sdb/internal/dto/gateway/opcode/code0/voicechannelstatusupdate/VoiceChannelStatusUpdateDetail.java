package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voicechannelstatusupdate;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch.VoiceChannelStatusUpdateService;

@Value
@EqualsAndHashCode(callSuper = true)
public class VoiceChannelStatusUpdateDetail extends EventDetailBase  {
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("guild_id")
	private String guildId;
	
	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceChannelStatusUpdateService.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
