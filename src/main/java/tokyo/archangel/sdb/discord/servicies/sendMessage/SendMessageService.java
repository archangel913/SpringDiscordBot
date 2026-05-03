package tokyo.archangel.sdb.discord.servicies.sendMessage;

import org.springframework.web.socket.WebSocketSession;

public interface SendMessageService {
	
	/**
	 * チャンネルIDを取得する
	 * -1の場合はゲートウェイを指す
	 * @return
	 */
	public long getChannelId();
	
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
	 * メッセージ送信メソッド<br>
	 * 非同期で実行される
	 */
	public void exec();

	/**
	 * 終了処理
	 */
	public void dispose();
}
