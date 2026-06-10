package tokyo.archangel.sdb.discord.component.voice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.Data;
import tokyo.archangel.sdb.discord.enumeration.ConnectingState;

@Data
public class VoiceChannelInfo {
	/**
	 * 音声websoketの接続先
	 */
	private String endpoint;

	/**
	 * ハートビート送信の間隔
	 */
	private int heartBeatInterval;

	/**
	 * チャンネルID
	 */
	private String channelId;

	/**
	 * ユーザーID
	 */
	private String userId;

	/**
	 * セッションID
	 */
	private String sessionId;

	/**
	 * ギルドID
	 */
	private String guildId;

	/**
	 * チャンネルに参加しているユーザー一覧
	 */
	private List<String> joinedUserIds = Collections.synchronizedList(new ArrayList<>());

	/**
	 * ステータス
	 */
	private String status;

	/**
	 * チャンネル開始時刻<br>
	 * JSTです（+9時間）
	 */
	private LocalDateTime startTime;

	/**
	 * ssrc
	 */
	private Integer ssrc;

	/**
	 * 音声接続に使用するトークン
	 */
	private String token;

	/**
	 * 音声接続情報
	 */
	private VoiceConnectionInfo info;

	/**
	 * websocketのGUID
	 */
	private String websocketGuid;

	/**
	 * 再接続前のwebsocketのGUID
	 */
	private String oldWebsocketGuid;

	/**
	 * マイクミュートか
	 */
	private boolean mute;

	/**
	 * スピーカーミュートか
	 */
	private boolean deaf;

	/**
	 * シーケンス
	 */
	private long seq = -1;

	/**
	 * 音声送信準備が整ったか
	 */
	private CompletableFuture<Void> readyFuture = new CompletableFuture<Void>();

	/**
	 * 接続状態
	 */
	private ConnectingState connectingState = ConnectingState.CONNECTING;

	/**
	 * 再接続を何回したか
	 */
	private int connectionFailCount = 0;
}
