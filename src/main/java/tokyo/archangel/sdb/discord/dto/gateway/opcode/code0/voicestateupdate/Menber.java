package tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicestateupdate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Menber {
	@JsonProperty("user")
	private User user;
	
	@JsonProperty("roles")
	private List<String> roles;
	
	@JsonProperty("premium_since")
	private Long premiumSince;

	@JsonProperty("pending")
	private Boolean pending;
	
	@JsonProperty("nick")
	private String nick;
	
	@JsonProperty("mute")
	private Boolean mute;
	
	@JsonProperty("joined_at")
	private String joinedAt;
	
	@JsonProperty("flags")
	private Integer flags;
 
	@JsonProperty("deaf")
	private Boolean deaf;

	@JsonProperty("communication_disabled_until")
	private String communicationDisabledUntil;
	
	@JsonProperty("banner")
	private String banner;
     
     @JsonProperty("avatar")
 	private String avatar;
}
