package tokyo.archangel.sdb.discord.enumeration;

/**
 * gatewayオペコードを表すenum
 */
public enum DispatchEvent {
	
	READY("READY"),
	GUILD_CREATE("GUILD_CREATE"),
	VOICE_SERVER_UPDATE("VOICE_SERVER_UPDATE");
	
	private final String eventName;
	
	private DispatchEvent(String eventName) {
		this.eventName = eventName;
	}
	
	public String getValue() {
		return eventName;
	}
}
