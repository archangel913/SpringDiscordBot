package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voicestateupdate;

import java.beans.Introspector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch.VoiceStateUpdateService;

@Value
@EqualsAndHashCode(callSuper = true)
public class VoiceStateUpdateDetail extends EventDetailBase  {
	
	@JsonProperty("member")	
	private Menber menber;

	@JsonProperty("user_id")
	private String userId;
	
	@JsonProperty("suppress")
	private Boolean suppress;
	
	@JsonProperty("session_id")
	private String sessionId;
	
	@JsonProperty("self_video")
	private Boolean selfVideo;
	
	@JsonProperty("self_mute")
	private Boolean selfMute;
	
	@JsonProperty("self_deaf")
	private Boolean selfDeaf;
	
	@JsonProperty("request_to_speak_timestamp")
	private Long requestToSpeakTimestamp;

	@JsonProperty("mute")
	private Boolean mute;
	
	@JsonProperty("guild_id")
	private String guildId;
	
	@JsonProperty("deaf")
	private Boolean deaf;
	
	@JsonProperty("connected_at")
	private Long connectedAt;
	
	@JsonProperty("channel_id")
	private String channelId;
	
	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = VoiceStateUpdateService.class.getSimpleName();
		return Introspector.decapitalize(className);
	}

}
