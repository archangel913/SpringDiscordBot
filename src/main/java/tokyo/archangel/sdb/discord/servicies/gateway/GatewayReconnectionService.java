package tokyo.archangel.sdb.discord.servicies.gateway;

import java.lang.Thread.State;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;

@Service
@Scope("prototype")
@Slf4j
public class GatewayReconnectionService {

	private GatewayConnectionService gatewayConnectionService;
	
	private GatewayInfo gatewayInfo;

	private Thread heartBeatThread;

	private Thread reconnectThread;

	public GatewayReconnectionService(
			@Lazy GatewayConnectionService gatewayConnectionService, GatewayInfo gatewayInfo) {
		this.gatewayConnectionService = gatewayConnectionService;
		this.gatewayInfo = gatewayInfo;
	}

	public void setHeartbeatThread(Thread heartbeatThread) {
		this.heartBeatThread = heartbeatThread;
	}

	@Async
	public CompletableFuture<Void> run() {
		reconnectThread = Thread.currentThread();

		if (heartBeatThread == null) {
			log.warn("heartBeatThreadがnullです。待機を実行しません");
			return CompletableFuture.completedFuture(null);
		}

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			log.trace("待機スレッドを破棄します");
			return CompletableFuture.completedFuture(null);
		}

		// 中断されなかったので、再接続を行う
		log.warn("ハートビートが確認できませんでした。");
		log.warn("再接続を行います");

		// すでに実行されているハートビートを停止させる
		log.debug("ハートビートスレッドを停止します");
		heartBeatThread.interrupt();
		// 終了まで待機
		while (heartBeatThread == null || heartBeatThread.getState() != State.TERMINATED) {
			try {
				heartBeatThread.join();
			} catch (InterruptedException e) {
				// 一度開始したスレッド中止処理はやめない
				// 例外を握りつぶして、再度待機する
			}
		}
		log.debug("ハートビートスレッドの停止が完了しました");

		// 再接続URL取得
		String reconnectingUrl = gatewayInfo.getReadyDetail().getResumeGatewayUrl();

		// ディスコード再接続
		gatewayConnectionService.connect(reconnectingUrl);
		log.info("再接続が完了しました");

		return CompletableFuture.completedFuture(null);
	}

	public void interrupt() {
		reconnectThread.interrupt();
	}
}
