package tokyo.archangel.sdb.voice;

/**
 * Discordへ音声を送信します<br>
 * ボイスチャンネルへの接続、切断もここで行います。
 */
public interface VoiceSender {
	
	/**
	 * 	 * ボイスチャンネルへ接続します
	 * @param guildId 接続するギルドID
	 * @param channelId 接続するチャンネルID
	 * @param selfMute マイクミュートするか
	 * @param selfDeaf スピーカーミュートするか
	 * @return 接続成功した場合true 失敗した場合false
	 */
	public void connect(String guildId, String channelId, boolean selfMute,  boolean selfDeaf);
	
	/**
	 * ボイスチャンネルから切断します
	 * @return 切断成功した場合true 失敗した場合false
	 */
	public void disconnect();
	
	/**
	 * ボイスチャンネルへデータを送信します<br>
	 * @param data 音声データ PCM音源です。
	 * @return
	 */
	public void send(byte[] data) throws InterruptedException;
	
	public void pause();
	
	public void resume();
}
