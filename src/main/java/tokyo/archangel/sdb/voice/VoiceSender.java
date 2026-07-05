package tokyo.archangel.sdb.voice;

/**
 * Discordへ音声を送信します。<br>
 * ボイスチャンネルへの接続、切断もここで行います。
 */
public interface VoiceSender {

	/* 
	 * ========================================================================================
	 * 操作系
	 * ========================================================================================
	 */

	/**
	 *  ボイスチャンネルへ接続します。
	 * @param guildId 接続するギルドID
	 * @param channelId 接続するチャンネルID
	 */
	public void connect(String guildId, String channelId);

	/**
	 * ボイスチャンネルから切断します。
	 */
	public void disconnect();

	/**
	 * ボイスチャンネルへデータを送信します。<br>
	 * 音声データのサイズに指定はありませんが、配列長3840を推奨します。<br>
	 * また、このメソッドは内部バッファが満杯だった場合、処理をブロックします。
	 * @exception InterruptedException
	 * @param data 音声データ PCM音源です。
	 */
	public void send(byte[] data) throws InterruptedException;

	/**
	 * バッファ内の音声データを削除します。<br>
	 * 例えば、送信する音声を切り替えたりする時に短時間の音声が残る場合、<br>
	 * このメソッドを呼ぶことで解決する場合があります。
	 */
	public void clearBuffer();

	/**
	 * 音声送信を停止します。<br>
	 * 音声停止中に呼び出しても副作用はありません。<br>
	 * sendメソッドの呼び出しを停止しても音声送信を停止できますが、<br>
	 * 内部バッファを保持する関係上、短時間の遅延が発生します。
	 */
	public void pause();

	/**
	 * 音声送信を再開します。<br>
	 * 音声再生中に呼び出しても副作用はありません。
	 */
	public void resume();

	/* 
	 * ========================================================================================
	 * 設定系
	 * ========================================================================================
	 */

	/**
	 * ボットのマイク状態を設定します。<br>
	 * {@link VoiceSender#connect(String, String)} の実行前に設定してください。<br>
	 * なお、初期値はfalseです。
	 * @deprecated このメソッドは試験的です。
	 * @param isMute
	 */
	public void setMute(boolean isMute);

	/**
	 * ボットのスピーカー状態を設定します。<br>
	 * {@link VoiceSender#connect(String, String)} の実行前に設定してください。<br>
	 * なお、初期値はfalseです。
	 * @deprecated このメソッドは試験的です。
	 * @param isMute
	 */
	public void setDeaf(boolean isDeaf);

	/**
	 * ボイスチャンネル接続時のイベントを登録します。<br>
	 * 発火タイミングは{@link VoiceSender#connect(String, String)}呼び出し直後です。
	 * @param process
	 */
	public void addConnectEvent(Runnable process);

	/**
	 * ボイスチャンネル接続時のイベントを削除します。<br>
	 * 引数には{@link VoiceSender#addConnectEvent(Runnable)}に渡した引数と同じインスタンスを渡してください。
	 * @return 削除に成功すればtrue
	 */
	public boolean removeConnectEvent(Runnable process);

	/**
	 * ボイスチャンネル切断時のイベントを登録します。<br>
	 * 発火タイミングは{@link VoiceSender#disconnect()}呼び出し直後です。
	 * @param process
	 */
	public void addDisconnectEvent(Runnable process);

	/**
	 * ボイスチャンネル切断時のイベントを削除します。<br>
	 * 引数には{@link VoiceSender#addConnectEvent(Runnable)}に渡した引数と同じインスタンスを渡してください。
	 * @return 削除に成功すればtrue
	 */
	public boolean removeDisconnectEvent(Runnable process);
}
