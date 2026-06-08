package tokyo.archangel.sdb.discord.servicies.sendMessage;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.socket.WebSocketSession;

public interface SendMessageService {

	/**
	 * チャンネルIDを取得する
	 * "gateway"の場合はゲートウェイを指す
	 * @return
	 */
	public String getChannelId();

	/**
	 * SSRC (Synchronization Source)を取得する
	 * @return
	 */
	public int getSsrc();

	/**
	 * ウェブソケットのセッションを取得する
	 * @return
	 */
	public WebSocketSession getSession();

	/**
	 * メッセージを送信する<br>
	 * 内部的には送信待ちキューに追加する
	 * @param message
	 */
	public void sendMessage(String message);

	/**
	 * メッセージを送信する<br>
	 * 内部的には送信待ちキューに追加する
	 * @param message
	 */
	public void sendMessage(byte[] message);

	/**
	 * メッセージ送信メソッド<br>
	 * 非同期で実行される
	 * @param channelId チャンネルID。gatewayの場合は"gateway"
	 */
	public CompletableFuture<Void> exec(String channelId);

	/**
	 * 終了処理
	 */
	public void close() throws IllegalStateException;
}
