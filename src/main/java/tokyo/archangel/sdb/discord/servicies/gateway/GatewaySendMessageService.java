package tokyo.archangel.sdb.discord.servicies.gateway;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;

/**
 * メッセージの送信とレート制限を管理するサービス<br>
 * こちらからの切断もこのクラスから行う
 */
@Service
@Slf4j
public class GatewaySendMessageService {
	
	private ApplicationProperties properties;
	
	private Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

	private WebSocketSession session;

	private Thread sendMesageThread;
	
	public GatewaySendMessageService(ApplicationProperties properties) {
		this.properties = properties;
	}
	/**
	 * websocket送信用セッションを設定する
	 * @param session
	 */
	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	/**
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		messageQueue.add(message);
	}

	/**
	 * 接続を閉じる
	 */
	public void close() {
		try {
			log.debug("websocketを切断します");
			session.close();
			log.info("discordから切断完了。");
		} catch (IOException e) {
			log.error("discordの切断中にエラーが発生しました。", e);
		}
	}

	/**
	 * メッセージ送信メソッド<br>
	 * 非同期で実行される
	 * @return
	 */
	@Async
	public CompletableFuture<Void> exec() {
		// スレッド重複起動防止
		if (sendMesageThread != null && sendMesageThread.isAlive()) {
			log.debug("重複しているのでスレッド起動しません。");
			return CompletableFuture.completedFuture(null);
		}
		
		log.debug("レート制限スレッド起動");
		sendMesageThread = Thread.currentThread();

		try {
			while (sendMesageThread.isAlive()) {
				// レート制限に達しないように待機する
				Thread.sleep(properties.getWebsocketSendRateLimit());
				
				// キューに送信内容があれば送信する				
				if (!messageQueue.isEmpty()) {
					String message = messageQueue.peek();
					if (session != null && session.isOpen()) {
						try {
							session.sendMessage(new TextMessage(message));
							log.trace("送信内容：" + message);
							messageQueue.remove();
						} catch (IOException e) {
							// 送信に失敗しているため再送する
							// ロジックとしては握りつぶす
							log.warn("送信に失敗しました。次回再送します。");
						}
					}
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return CompletableFuture.completedFuture(null);
	}

	@PreDestroy
	public void stopSendMessageThread() {
		sendMesageThread.interrupt();
	}
}
