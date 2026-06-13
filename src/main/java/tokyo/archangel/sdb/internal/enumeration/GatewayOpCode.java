package tokyo.archangel.sdb.internal.enumeration;

/**
 * gatewayオペコードを表すenum
 */
public enum GatewayOpCode {
	/** opcode 0 */
	DISPATCH(0),
	/** opcode 1 */
	HEARTBEAT(1),
	/** opcode 2 */
	IDENTIFY(2),
	/** opcode 3 */
	PRESENCE_UPDATE(3),
	/** opcode 4 */
	VOICE_STATE_UPDATE(4),
	/** opcode 6 */
	RESUME(6),
	/** opcode 7 */
	RECONNECT(7),
	/** opcode 8 */
	REQUEST_GUILD_MEMBERS(8),
	/** opcode 9 */
	INVALID_SESSION(9),
	/** opcode 10 */
	HELLO(10),
	/** opcode 11 */
	HEARTBEAT_ACK(11),
	/** opcode 31 */
	REQUEST_SOUNDBOARD_SOUNDS(31);
	
	private final int opcode;
	
	private GatewayOpCode(int opcode) {
		this.opcode = opcode;
	}
	
	public int getValue() {
		return opcode;
	}
}
