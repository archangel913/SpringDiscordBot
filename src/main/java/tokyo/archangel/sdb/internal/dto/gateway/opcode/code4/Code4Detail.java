package tokyo.archangel.sdb.internal.dto.gateway.opcode.code4;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code4Detail {
	@JsonProperty("guild_id")
	private String guildId;
	
	@JsonProperty("channel_id")
	private String channelId;
	
	@JsonProperty("self_mute")
	private Boolean selfMute;
	
	@JsonProperty("self_deaf")
	private Boolean selfDeaf;
}
