package tokyo.archangel.sdb.discord.enumeration;

/**
 * voiceオペコードを表すenum
 */
public enum VoiceOpCode {
	/** opcode 0 */
	IDENTIFY(0),
	/** opcode 2 */
	READY(2),
	/** opcode 3 */
	HEARTBEAT(3),
	/** opcode 6 */
	HEARTBEAT_ACK(6),
	/** opcode 8 */
	HELLO(8);
	
	private final int opcode;
	
	private VoiceOpCode(int opcode) {
		this.opcode = opcode;
	}
	
	public int getValue() {
		return opcode;
	}
}
