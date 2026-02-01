package tokyo.archangel.sdb.discord.enumeration;

import java.util.List;

public enum Intent {
	/**
	 * (0)
	 */
	NONE(0),

	/**
	 * ギルド全般に関するインテント (1 << 0) <br>
	 * - GUILD_CREATE<br>
	 * - GUILD_UPDATE<br>
	 * - GUILD_DELETE<br>
	 * - GUILD_ROLE_CREATE<br>
	 * - GUILD_ROLE_UPDATE<br>
	 * - GUILD_ROLE_DELETE<br>
	 * - CHANNEL_CREATE<br>
	 * - CHANNEL_UPDATE<br>
	 * - CHANNEL_DELETE<br>
	 * - CHANNEL_PINS_UPDATE<br>
	 * - THREAD_CREATE<br>
	 * - THREAD_UPDATE<br>
	 * - THREAD_DELETE<br>
	 * - THREAD_LIST_SYNC<br>
	 * - THREAD_MEMBER_UPDATE<br>
	 * - THREAD_MEMBERS_UPDATE<br>
	 * - STAGE_INSTANCE_CREATE<br>
	 * - STAGE_INSTANCE_UPDATE<br>
	 * - STAGE_INSTANCE_DELETE
	 */
	GUILDS(1),

	/**
	 * ギルドメンバーに関するインテント (1 << 1)<br>
	 *   - GUILD_MEMBER_ADD<br>
	 * - GUILD_MEMBER_UPDATE<br>
	 * - GUILD_MEMBER_REMOVE<br>
	 * - THREAD_MEMBERS_UPDATE
	 */
	GUILD_MEMBERS(2),

	/**
	 * ギルド管理に関するインテント(1 << 2)<br>
	 * 特権インテントです！<br>
	 * - GUILD_AUDIT_LOG_ENTRY_CREATE<br>
	 * - GUILD_BAN_ADD<br>
	 * - GUILD_BAN_REMOVE
	 */
	GUILD_MODERATION(4),

	/**
	 * 絵文字・サウンドボードに関するインテント (1 << 3)<br>
	 * - GUILD_EMOJIS_UPDATE<br>
	 * - GUILD_STICKERS_UPDATE<br>
	 * - GUILD_SOUNDBOARD_SOUND_CREATE<br>
	 * - GUILD_SOUNDBOARD_SOUND_UPDATE<br>
	 * - GUILD_SOUNDBOARD_SOUND_DELETE<br>
	 * - GUILD_SOUNDBOARD_SOUNDS_UPDATE
	 */
	GUILD_EXPRESSIONS(8),

	/**
	 * インタラクションに関するインテント (1 << 4)<br>
	 * - GUILD_INTEGRATIONS_UPDATE<br>
	 * - INTEGRATION_CREATE<br>
	 * - INTEGRATION_UPDATE<br>
	 * - INTEGRATION_DELETE
	 */
	GUILD_INTEGRATIONS(16),

	/**
	 *  webhookに関するインテント(1 << 5)<br>
	 * - WEBHOOKS_UPDATE
	 */
	GUILD_WEBHOOKS(32),

	/**
	 * 招待に関するインテント (1 << 6)<br>
	 * - INVITE_CREATE<br>
	 * - INVITE_DELETE
	 */
	GUILD_INVITES(64),

	/**
	 * 音声に関するインテント (1 << 7)<br>
	 * - VOICE_CHANNEL_EFFECT_SEND<br>
	 * - VOICE_STATE_UPDATE
	 */
	GUILD_VOICE_STATES(128),

	/**
	 *  プレゼンスに関するインテント (1 << 8)<br>
	 *  特権インテントです！<br>
	 * - PRESENCE_UPDATE
	 */
	GUILD_PRESENCES(256),

	/**
	 * メッセージに関するインテント (1 << 9)<br>
	 * - MESSAGE_CREATE<br>
	 * - MESSAGE_UPDATE<br>
	 * - MESSAGE_DELETE<br>
	 * - MESSAGE_DELETE_BULK
	 */
	GUILD_MESSAGES(512),

	/**
	 * リアクションに関するインテント (1 << 10)<br>
	 * - MESSAGE_REACTION_ADD<br>
	 * - MESSAGE_REACTION_REMOVE<br>
	 * - MESSAGE_REACTION_REMOVE_ALL<br>
	 * - MESSAGE_REACTION_REMOVE_EMOJI
	 */
	GUILD_MESSAGE_REACTIONS(1024),

	/**
	 * 入力中表示に関するインテント (1 << 11)<br>
	 * - TYPING_START
	 */
	GUILD_MESSAGE_TYPING(2048),

	/**
	 * DMに関するインテント (1 << 12)<br>
	 * - MESSAGE_CREATE<br>
	 * - MESSAGE_UPDATE<br>
	 * - MESSAGE_DELETE<br>
	 * - CHANNEL_PINS_UPDATE
	 */
	DIRECT_MESSAGES(4096),

	/**
	 *  DMのリアクションに関するインテント (1 << 13)<br>
	 * - MESSAGE_REACTION_ADD<br>
	 * - MESSAGE_REACTION_REMOVE<br>
	 * - MESSAGE_REACTION_REMOVE_ALL<br>
	 * - MESSAGE_REACTION_REMOVE_EMOJI
	 */
	DIRECT_MESSAGE_REACTIONS(8192),

	/**
	 * DMの入力中表示に関するインテント (1 << 14)<br>
	 * - TYPING_START
	 */
	DIRECT_MESSAGE_TYPING(16384),

	/**
	 * メッセージに関するインテント (1 << 15)<br>
	 * 特権インテントです！
	 */
	MESSAGE_CONTENT(32768),

	/**
	 * スケジュールされたイベントに関するインテント (1 << 16)<br>
	 * - GUILD_SCHEDULED_EVENT_CREATE<br>
	 * - GUILD_SCHEDULED_EVENT_UPDATE<br>
	 * - GUILD_SCHEDULED_EVENT_DELETE<br>
	 * - GUILD_SCHEDULED_EVENT_USER_ADD<br>
	 * - GUILD_SCHEDULED_EVENT_USER_REMOVE
	 */
	GUILD_SCHEDULED_EVENTS(65536),

	/**
	 * (1 << 20)<br>
	 * - AUTO_MODERATION_RULE_CREATE<br>
	 * - AUTO_MODERATION_RULE_UPDATE<br>
	 * - AUTO_MODERATION_RULE_DELETE
	 */
	AUTO_MODERATION_CONFIGURATION(1048576),

	/**
	 * (1 << 21)<br>
	 * - AUTO_MODERATION_ACTION_EXECUTION
	 */
	AUTO_MODERATION_EXECUTION(2097152),

	/**
	 * (1 << 24)<br>
	 * - MESSAGE_POLL_VOTE_ADD<br>
	 * - MESSAGE_POLL_VOTE_REMOVE
	 */
	GUILD_MESSAGE_POLLS(16777216),

	/**
	 * (1 << 25)<br>
	 * - MESSAGE_POLL_VOTE_ADD<br>
	 * - MESSAGE_POLL_VOTE_REMOVE
	 */
	DIRECT_MESSAGE_POLLS(33554432);

	private final int intent;

	private Intent(int intent) {
		this.intent = intent;
	}

	public int getValue() {
		return intent;
	}

	/**
	 * 引数のインテントを含んだフラグを作成する
	 * @param intents
	 * @return
	 */
	public static int buildIntent(List<Intent> intents) {
		int result = Intent.NONE.getValue();
		for (Intent i : intents) {
			result = result | i.getValue();
		}
		return result;
	}

	/**
	 * すべてのインテントを含んだフラグを作成する
	 * @return
	 */
	public static int buildAllIntent() {
		int result = Intent.NONE.getValue();
		for (Intent i : Intent.values()) {
			result = result | i.getValue();
		}
		return result;
	}
}
