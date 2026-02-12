package tokyo.archangel.sdb.discord.servicies.gateway;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;

/**
 * ハートビートを監視するクラス<br>
 * ハートビートが返ってこなかった時にハートビートを停止する。
 */
@Service
@Slf4j
public class GatewayHeartBeatCheckService {
	private GatewayInfo gatewayInfo;

	private Thread heartBeatThread;

	private Thread heartBeatCheakThread;
	
	private final AtomicBoolean isRunning = new AtomicBoolean(false);

	private Queue<LocalDateTime> queue = new ConcurrentLinkedQueue<LocalDateTime>();

	public GatewayHeartBeatCheckService(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	public void setHeartbeatThread(Thread heartbeatThread) {
		this.heartBeatThread = heartbeatThread;
	}

	public void addWait() {
		queue.add(LocalDateTime.now().plusSeconds(10));
	}

	public void remove() {
		queue.poll();
	}

	/**
	 * ハートビートが返ってきているかを確認するメソッド<br>
	 * 一度起動されたら、プログラム終了まで走り続ける
	 * @return
	 */
	@Async
	public void exec() {
		// 重複起動防止
		if (!isRunning.compareAndSet(false, true)) {
			log.debug("heartBeatCheakThreadがすでに存在します。");
			return;
		}

		heartBeatCheakThread = Thread.currentThread();
		heartBeatCheakThread.setName("heartBeatCheak");
		log.debug("heartBeatCheakThreadを起動します");

		try {
			while (!heartBeatCheakThread.isInterrupted()) {
				Thread.sleep(10000);

				// ハートビートが返ってきているか確認
				LocalDateTime time = queue.peek();
				LocalDateTime now = LocalDateTime.now();
				if (time != null && time.isBefore(now)) {
					log.warn("ハートビートが確認できませんでした。");
					gatewayInfo.setReconnectMode(ReconnectMode.NORMAL);
					break;
				}
			}
			// すでに実行されているハートビートを停止させる
			log.warn("ハートビートスレッドを停止します");
			if (heartBeatThread == null) {
				log.warn("heartBeatThreadがnullです。停止処理を実行しません");
				heartBeatThread.interrupt();
			}
			queue.clear();

		} catch (InterruptedException e) {
			// 終了処理を行うため握りつぶす
			Thread.currentThread().interrupt();
		}

		log.debug("ハートビートチェックスレッドが完了しました");
		isRunning.set(false);
	}

	@PreDestroy
	public void stopHeartBeatCheak() {
		if (heartBeatCheakThread != null) {
			heartBeatCheakThread.interrupt();
		}

	}
}
