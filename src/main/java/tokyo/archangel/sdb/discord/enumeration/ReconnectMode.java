package tokyo.archangel.sdb.discord.enumeration;

public enum ReconnectMode {
	NONE("none"),
	NORMAL("normal"),
	HARD("hard");
	
	private final String mode;
	
	private ReconnectMode(String mode) {
		this.mode = mode;
	}
	
	public String getValue() {
		return mode;
	}
}
