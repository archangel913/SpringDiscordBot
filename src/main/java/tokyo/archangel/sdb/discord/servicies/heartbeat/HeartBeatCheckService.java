package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;

/**
 * ハートビートが返ってきているか監視するクラス<br>
 * ハートビートが返ってこなかった時にハートビートを停止する。
 */
@Service
@Scope("prototype")
@Slf4j
public class HeartBeatCheckService {
	private GatewayInfo gatewayInfo;

	private HeartBeatService heartBeatService;

	private ServiceThreadStatus status;

	private Queue<LocalDateTime> queue = new ConcurrentLinkedQueue<LocalDateTime>();
	
	private Thread currentThread;

	public HeartBeatCheckService(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	public void setHeartbeatService(HeartBeatServiceImpl heartBeatService) {
		this.heartBeatService = heartBeatService;
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
		if(status == ServiceThreadStatus.ACTIVE) {
			log.debug("ハートビートチェックスレッドがすでに起動しています");
			return;
		}
		status = ServiceThreadStatus.ACTIVE;
		currentThread = Thread.currentThread();
		currentThread.setName("heartBeatCheak");
		log.debug("ハートビートチェックスレッドを起動します");

		try {
			while (status == ServiceThreadStatus.ACTIVE) {
				Thread.sleep(10000);

				// ハートビートが返ってきているか確認
				LocalDateTime time = queue.peek();
				LocalDateTime now = LocalDateTime.now();
				if (time != null && time.isBefore(now)) {
					log.warn("ハートビートが確認できませんでした。");
					// 音声ハートビートにも対応
					gatewayInfo.setReconnectMode(ReconnectMode.NORMAL);
					break;
				}
			}
		} catch (InterruptedException e) {
			// 終了処理を行うため握りつぶす
			Thread.currentThread().interrupt();
		} finally {
			status = ServiceThreadStatus.TERMINATING;
			// 実行されているハートビートを停止させる
			log.warn("ハートビートスレッドを停止します");
			if (heartBeatService != null) {
				heartBeatService.dispose();
			} else {
				log.warn("ハートビートサービスがnullです。サービスの停止を行いません。");
			}
			queue.clear();
			status = ServiceThreadStatus.TERMINATED;
			log.debug("ハートビートチェックスレッドが完了しました");
		}
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
