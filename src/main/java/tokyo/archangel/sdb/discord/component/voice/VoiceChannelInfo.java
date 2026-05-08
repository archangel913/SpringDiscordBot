package tokyo.archangel.sdb.discord.component.voice;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class VoiceChannelInfo {
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
	 * ステータス
	 */
	private String status;

	/**
	 * チャンネル開始時刻<br>
	 * JSTです（+9時間）
	 */
	private LocalDateTime startTime;

	/**
	 * シーケンス
	 */
	private long seq = -1;

	@Override
	public String toString() {
		return "\n"
				+ "チャンネルID: " + channelId + "\n"
				+ "ギルドID: " + guildId + "\n"
				+ "ステータス: " + status + "\n"
				+ "チャンネル開始時刻: " + startTime + "\n";
	}
}
