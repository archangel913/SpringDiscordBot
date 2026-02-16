package tokyo.archangel.sdb.discord.enumeration;

public enum GatewayWebsocketCode {
	
	/**
	 * 正常終了
	 */
	NORMAL_CLOSURE(1000),
	
	/**
	 * 退出
	 */
	GOING_AWAY(1001),
	
	/**
	 * 不明なエラー
	 */
	UNKNOWN_ERROR(4000),
	
	/**
	 * 不明なOPコード
	 */
	UNKNOWN_OPCODE(4001),
	
	/**
	 * デーコードエラー
	 */
	DECODE_ERROR(4002),
	
	/**
	 * 未認証
	 */
	NOT_AUTHENTICATED(4003),
	
	/**
	 * 認証失敗
	 */
	AUTHENTICATION_FAILED(4004),
	
	/**
	 * 認証済み
	 */
	ALREADY_AUTHENTICATED(4005),
	
	/**
	 * 不正なシーケンス
	 */
	INVALID_SEQ(4007),
	
	/**
	 * レート制限
	 */
	RATE_LIMITED(4008),
	
	/**
	 * セッションタイムアウト
	 */
	SESSION_TIMED_OUT(4009),
	
	/**
	 * 不正なシャード
	 */
	INVALID_SHARD(4010),
	
	/**
	 * シャード必須
	 */
	SHARDING_REQUIRED(4011),
	
	/**
	 * 不正なAPIバージョン
	 */
	INVALID_API_VERSION(4012),
	
	/**
	 * 不正なインテンツ
	 */
	INVALID_INTENTS(4013),
	
	/**
	 * 許可されていないインテンツ
	 */
	DISALLOWED_INTENTS(4014),
	
	/**
	 * その他失敗コード
	 */
	UNKNOWN_CODE(-1);

	private final int code;

	private GatewayWebsocketCode(int code) {
		this.code = code;
	}

	public int getValue() {
		return code;
	}
	
	public static GatewayWebsocketCode getGatewayWebsocketCode(int num) {
		for(GatewayWebsocketCode code : GatewayWebsocketCode.values()) {
			if(code.getValue() == num) {
				return code;
			}
		}
		return UNKNOWN_CODE;
	}

	public boolean canReconnect() {
		return !(this == AUTHENTICATION_FAILED
				|| this == INVALID_SHARD
				|| this == SHARDING_REQUIRED
				|| this == INVALID_INTENTS
				|| this == DISALLOWED_INTENTS);
	}
}
