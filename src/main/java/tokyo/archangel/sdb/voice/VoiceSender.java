package tokyo.archangel.sdb.voice;

/**
 * Discordへ音声を送信します<br>
 * ボイスチャンネルへの接続、切断もここで行います。
 */
public interface VoiceSender {
	
	/**
	 *  ボイスチャンネルへ接続します
	 * @param guildId 接続するギルドID
	 * @param channelId 接続するチャンネルID
	 * @param selfMute マイクミュートするか
	 * @param selfDeaf スピーカーミュートするか
	 */
	public void connect(String guildId, String channelId, boolean selfMute,  boolean selfDeaf);
	
	/**
	 * ボイスチャンネルから切断します
	 */
	public void disconnect();
	
	/**
	 * ボイスチャンネルへデータを送信します<br>
	 * 音声データのサイズに指定はありませんが、配列長3840を推奨します。
	 * @param data 音声データ PCM音源です。
	 */
	public void send(byte[] data) throws InterruptedException;
	
	/**
	 * 音声送信を停止します<br>
	 * 音声停止中に呼び出しても副作用はありません<br>
	 * sendメソッドの呼び出しを停止しても音声送信を停止できますが、最大1秒の遅延が発生します。
	 */
	public void pause();
	
	/**
	 * 音声送信を再開します<br>
	 * 音声再生中に呼び出しても副作用はありません
	 */
	public void resume();
}
