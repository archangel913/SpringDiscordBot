package tokyo.archangel.sdb.discord.enumeration;

public enum ServiceThreadStatus {
	INITIALIZING(1),
	INITIALIZED(2),
	ACTIVE(3),
	TERMINATING(4),
	TERMINATED(5);
	
	private final int statusNumber;
	
	private ServiceThreadStatus(int statusNumber) {
		this.statusNumber = statusNumber;
	}
	
	public int getValue() {
		return statusNumber;
	}
}
