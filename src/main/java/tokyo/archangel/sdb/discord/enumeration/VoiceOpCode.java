package tokyo.archangel.sdb.discord.enumeration;

/**
 * voiceオペコードを表すenum
 */
public enum VoiceOpCode {
	/** opcode 0 */
	IDENTIFY(0),
	/** opcode 1 */
	SELECT_PROTOCOL (1),
	/** opcode 2 */
	READY(2),
	/** opcode 3 */
	HEARTBEAT(3),
	/** opcode 4 */
	SESSION_DISCRIPTION(4),
	/** opcode 5 */
	SPEAKING(5),
	/** opcode 6 */
	HEARTBEAT_ACK(6),
	/** opcode 7 */
	RESUME(7),
	/** opcode 8 */
	HELLO(8),
	/** opcode 11 */
	CLIENTS_CONNECT(11),
	/** opcode 13 */
	CLIENTS_DISCONNECT(13),
	/** opcode 22 */
	DAVE_PROTOCOL_EXECUTE_TRANSITION(22),
	/** opcode 23 */
	DAVE_PROTOCOL_READY_FOR_TRANSITION(23);
	
	private final int opcode;
	
	private VoiceOpCode(int opcode) {
		this.opcode = opcode;
	}
	
	public int getValue() {
		return opcode;
	}
}
