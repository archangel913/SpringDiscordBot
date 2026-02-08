package tokyo.archangel.sdb.discord.servicies.gateway;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;

@Service
@Slf4j
public class GatewayHeartBeatCheckService {
	private GatewayInfo gatewayInfo;

	private Thread heartBeatThread;

	private Thread heartBeatCheakThread;

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

	@Async
	public CompletableFuture<Void> run() {
		heartBeatCheakThread = Thread.currentThread();
		if (heartBeatThread == null) {
			log.warn("heartBeatThreadがnullです。待機を実行しません");
			return CompletableFuture.completedFuture(null);
		}

		try {
			while (true) {
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
		} catch (InterruptedException e) {
			// 終了処理を行うため握りつぶす
			Thread.currentThread().interrupt();
		}

		// すでに実行されているハートビートを停止させる
		log.debug("ハートビートスレッドを停止します");
		heartBeatThread.interrupt();
		queue.clear();

		log.debug("ハートビートチェックスレッドが完了しました");

		return CompletableFuture.completedFuture(null);
	}

	@PreDestroy
	public void stopHeartBeatCheak() {
		heartBeatCheakThread.interrupt();
	}
}
