package tokyo.archangel.sdb.internal.enumeration;

public enum ReconnectMode {
	/**
	 * 初回起動
	 */
	NONE("none"),
	
	/**
	 * 再接続用URL使用
	 */
	NORMAL("normal"),
	
	/**
	 * トークンを使用して再接続
	 */
	HARD("hard");
	
	private final String mode;
	
	private ReconnectMode(String mode) {
		this.mode = mode;
	}
	
	public String getValue() {
		return mode;
	}
}
