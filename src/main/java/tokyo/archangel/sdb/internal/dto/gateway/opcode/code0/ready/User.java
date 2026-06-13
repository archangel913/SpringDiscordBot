package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.ready;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class User {
	@JsonProperty("verified")
	private Boolean verified;
	
	@JsonProperty("username")
	private String username;
	
	@JsonProperty("primary_guild")
	private String primaryGuild;
	
	@JsonProperty("mfa_enabled")
	private Boolean mfaEnabled;

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("global_name")
	private String globalName;
	
	@JsonProperty("flags")
	private Long flags;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("discriminator")
	private String discriminator;

	@JsonProperty("clan")
	private String clan;
	
	@JsonProperty("bot")
	private Boolean bot;
	
	@JsonProperty("avatar")
	private String avatar;
}
