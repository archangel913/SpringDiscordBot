package tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready;

import java.beans.Introspector;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.dispatch.ReadyEventService;

@Value
@EqualsAndHashCode(callSuper = true)
public class ReadyDetail extends EventDetailBase {
	@JsonProperty("v")
	private Integer version;

	@JsonProperty("user_settings")
	private UserSettings userSetting;

	@JsonProperty("user")
	private User user;

	@JsonProperty("session_type")
	private String sessionType;

	@JsonProperty("session_id")
	private String sessionId;

	@JsonProperty("resume_gateway_url")
	private String resumeGatewayUrl;

	@JsonProperty("relationships")
	private List<String> relationships;

	@JsonProperty("private_channels")
	private List<String> privateChannels;

	@JsonProperty("presences")
	private List<String> presences;

	@JsonProperty("guilds")
	private List<Guild> guilds;

	@JsonProperty("guild_join_requests")
	private List<String> guildzJoinRequests;

	@JsonProperty("geo_ordered_rtc_regions")
	private List<String> geoOrderedRtczRegions;

	@JsonProperty("game_relationships")
	private List<String> game_Relationships;

	@JsonProperty("auth")
	private Auth auth;

	@JsonProperty("application")
	private Application application;

	@JsonProperty("_trace")
	private List<String> trace;

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = ReadyEventService.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
