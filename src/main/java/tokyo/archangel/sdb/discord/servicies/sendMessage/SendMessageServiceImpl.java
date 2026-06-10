package tokyo.archangel.sdb.discord.servicies.sendMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;

/**
 * メッセージの送信とレート制限を管理するサービス<br>
 * こちらからの切断もこのクラスから行う
 */
@Slf4j
public class SendMessageServiceImpl implements SendMessageService {
	private static int threadNumber = 0;

	private final Queue<WebSocketMessage<?>> messageQueue = new ConcurrentLinkedQueue<>();

	private final WebSocketSession session;

	private final ApplicationProperties properties;

	private volatile ServiceThreadStatus status = ServiceThreadStatus.INITIALIZING;

	private Thread currentThread;

	private String channelId;

	private int ssrc;

	public SendMessageServiceImpl(ApplicationProperties properties, WebSocketSession session) {
		this.properties = properties;
		this.session = session;
	}

	@Override
	public String getChannelId() {
		return channelId;
	}

	@Override
	public int getSsrc() {
		return ssrc;
	}

	@Override
	public WebSocketSession getSession() {
		return session;
	}

	@Override
	public void sendMessage(String message) {
		messageQueue.add(new TextMessage(message));
	}

	@Override
	public void sendMessage(byte[] message) {
		messageQueue.add(new BinaryMessage(message));

	}

	@Async
	public CompletableFuture<Void> exec(String channelId) {
		if (status == ServiceThreadStatus.ACTIVE) {
			log.debug("メッセージ送信スレッドがすでに起動しています");
			return CompletableFuture.completedFuture(null);
		}
		status = ServiceThreadStatus.ACTIVE;
		currentThread = Thread.currentThread();
		currentThread.setName("sendMesage-" + threadNumber + "-" + channelId);
		threadNumber++;
		log.debug("メッセージ送信スレッド起動");

		try {
			while (status == ServiceThreadStatus.ACTIVE) {
				// キューに送信内容があれば送信する	
				WebSocketMessage<?> message;
				if ((message = messageQueue.poll()) != null) {
					if (session != null && session.isOpen()) {
						try {
							session.sendMessage(message);
							log.trace("送信内容：" + message.getPayload());
						} catch (IOException e) {
							// 送信に失敗しているため再送する
							// ロジックとしては握りつぶす
							log.warn("送信に失敗しました。次回再送します。");
						} finally {
							// レート制限に達しないように待機する
							Thread.sleep(properties.getWebsocketSendRateLimit());
						}
					}
				} else {
					// ビジーウェイト防止
					Thread.sleep(50);
				}
			}
		} catch (InterruptedException e) {
			// finallyで問題なく終了処理を行うために握りつぶす
		} catch (Exception e) {
			log.error("メッセージ送信スレッドが異常終了しました。", e);
		} finally {
			shutdown();
		}
		return CompletableFuture.completedFuture(null);
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setSsrc(int ssrc) {
		this.ssrc = ssrc;
	}

	@Override
	public void close() throws IllegalStateException {
		if (currentThread == null) {
			throw new IllegalStateException("停止対象のスレッドが不明です。");
		}
		// 既に完全に終了している場合は重ねて処理しない
		if (status == ServiceThreadStatus.TERMINATED) {
			return;
		}

		// 終了処理中でなければ、ステータスを TERMINATING に移行
		if (status != ServiceThreadStatus.TERMINATING) {
			status = ServiceThreadStatus.TERMINATING;
		}

		log.debug("メッセージ送信スレッドを終了します。");
		currentThread.interrupt();
	}

	private void shutdown() {
		try {
			log.debug("websocketを切断します");
			session.close();
			log.info("discordから切断完了。");
		} catch (IOException e) {
			log.error("discordの切断中にエラーが発生しました。", e);
		}
		status = ServiceThreadStatus.TERMINATED;
	}
}
