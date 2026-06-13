package tokyo.archangel.sdb.internal.enumeration;

public enum ConnectingState {
	CONNECTING(1),
	CONNECTED(2),
	RESUMING(3),
	RECONNECTING(4),
	DISCONNECTING(5),
	DISCONNECTED(6);

	private final int statusNumber;

	private ConnectingState(int statusNumber) {
		this.statusNumber = statusNumber;
	}

	public int getValue() {
		return statusNumber;
	}
}
