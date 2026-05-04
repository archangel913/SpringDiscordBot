package tokyo.archangel.sdb.discord.enumeration;

/**
 * gatewayオペコードを表すenum
 */
public enum DispatchEvent {
	
	READY("READY"),
	GUILD_CREATE("GUILD_CREATE"),
	VOICE_SERVER_UPDATE("VOICE_SERVER_UPDATE"),
	VOICE_STATE_UPDATE("VOICE_STATE_UPDATE"),
	VOICE_CHANNEL_START_TIME_UPDATE("VOICE_CHANNEL_START_TIME_UPDATE"),
	VOICE_CHANNEL_STATUS_UPDATE("VOICE_CHANNEL_STATUS_UPDATE");
	
	private final String eventName;
	
	private DispatchEvent(String eventName) {
		this.eventName = eventName;
	}
	
	public String getValue() {
		return eventName;
	}
}
