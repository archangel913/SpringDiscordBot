package tokyo.archangel.sdb.discord.enumeration;

public enum ServiceThreadStatus {
	ACTIVE(1),
	TERMINATING(2),
	TERMINATED(3);
	
	private final int statusNumber;
	
	private ServiceThreadStatus(int statusNumber) {
		this.statusNumber = statusNumber;
	}
	
	public int getValue() {
		return statusNumber;
	}
}
