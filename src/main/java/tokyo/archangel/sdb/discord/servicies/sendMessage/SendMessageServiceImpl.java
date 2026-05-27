package tokyo.archangel.sdb.discord.servicies.sendMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;

/**
 * メッセージの送信とレート制限を管理するサービス<br>
 * こちらからの切断もこのクラスから行う
 */
@Service
@Scope("prototype")
@Slf4j
public class SendMessageServiceImpl implements SendMessageService {
	private ApplicationProperties properties;

	private Queue<WebSocketMessage<?>> messageQueue = new ConcurrentLinkedQueue<>();

	private WebSocketSession session;

	private ServiceThreadStatus status;
	
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
	public void exec() {
		if(status == ServiceThreadStatus.ACTIVE) {
			log.debug("メッセージ送信スレッドがすでに起動しています");
			return;
		}
		status = ServiceThreadStatus.ACTIVE;
		currentThread = Thread.currentThread();
		currentThread.setName("sendMesage");
		log.debug("メッセージ送信スレッド起動");

		try {
			while (status == ServiceThreadStatus.ACTIVE) {
				// レート制限に達しないように待機する
				Thread.sleep(properties.getWebsocketSendRateLimit());

				// キューに送信内容があれば送信する				
				if (!messageQueue.isEmpty()) {
					WebSocketMessage<?> message = messageQueue.peek();
					if (session != null && session.isOpen()) {
						try {
							session.sendMessage(message);
							log.trace("送信内容：" + message.getPayload());
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
		} finally {
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
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public void setSsrc(int ssrc) {
		this.ssrc = ssrc;
	}

	@PreDestroy
	public void dispose() {
		if(status != ServiceThreadStatus.ACTIVE) {
			return;
		}
		status = ServiceThreadStatus.TERMINATING;
		currentThread.interrupt();
	}
}
