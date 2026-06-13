package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voicestateupdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class User {
	@JsonProperty("username")
	private String userName;
	
	@JsonProperty("public_flags")
	private Integer publicFlags;
	
	@JsonProperty("primary_guild")
	private UserPrimaryGuild primaryGuild;
	
	@JsonProperty("id")
	private String id;

	@JsonProperty("global_name")
	private String globalName;
	
	@JsonProperty("display_name_styles")
	private String displayNameStyles;
	
	@JsonProperty("display_name")
	private String displayName;
	
	@JsonProperty("discriminator")
	private String discriminator;
	
	@JsonProperty("collectibles")
	private Collectibles collectibles;
	
	@JsonProperty("bot")
	private Boolean bot;
	
	@JsonProperty("avatar_decoration_data")
	private AvatarDecorationData avatarDecorationData;

	@JsonProperty("avatar")
	private String avatar;
}
