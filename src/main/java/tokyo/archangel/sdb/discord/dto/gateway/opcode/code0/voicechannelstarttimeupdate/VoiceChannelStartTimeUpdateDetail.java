package tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicechannelstarttimeupdate;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch.VoiceChannelStartTimeUpdateService;

@Value
@EqualsAndHashCode(callSuper = true)
public class VoiceChannelStartTimeUpdateDetail extends EventDetailBase  {

	@JsonProperty("voice_start_time")
	private Long voiceStartTime;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("guild_id")
	private String guildId;
	
	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceChannelStartTimeUpdateService.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
