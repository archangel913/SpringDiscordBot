package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voicestateupdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class UserPrimaryGuild {
	@JsonProperty("tag")
	private String tag;
	
	@JsonProperty("badge")
	private String badge;
}
