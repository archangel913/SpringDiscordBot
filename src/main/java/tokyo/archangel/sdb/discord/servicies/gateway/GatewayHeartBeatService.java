package tokyo.archangel.sdb.discord.servicies.gateway;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tools.jackson.databind.ObjectMapper;

/**
 * ハートビートを送信するクラス
 */
@Service
@Slf4j
public class GatewayHeartBeatService {

	private GatewayHeartBeatCheckService gatewayHeartBeatCheckService;

	private GatewaySendMessageService gatewaySendMessageService;

	private GatewayInfo gatewayInfo;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Random random = new Random();

	private Thread heartBeatThread;
	
	private final AtomicBoolean isRunning = new AtomicBoolean(false);

	private long threadNumber = 0;

	public GatewayHeartBeatService(GatewayHeartBeatCheckService gatewayHeartBeatCheckService,
			GatewayInfo gatewayInfo, GatewaySendMessageService gatewaySendMessageService) {
		this.gatewayHeartBeatCheckService = gatewayHeartBeatCheckService;
		this.gatewayInfo = gatewayInfo;
		this.gatewayHeartBeatCheckService = gatewayHeartBeatCheckService;
		this.gatewaySendMessageService = gatewaySendMessageService;
	}

	@Async
	public synchronized void exec(int interval) {
		// 重複起動防止
		if (!isRunning.compareAndSet(false, true)) {
			log.debug("heartBeatThreadがすでに存在します。");
			return;
		}

		heartBeatThread = Thread.currentThread();
		heartBeatThread.setName("sendHeartBeat-" + ++threadNumber);
		gatewayHeartBeatCheckService.setHeartbeatThread(heartBeatThread);
		log.debug("heartBeatThreadを開始します。");
		try {
			Thread.sleep((long) (interval * random.nextDouble()));
			while (!heartBeatThread.isInterrupted()) {
				log.debug("バックグラウンドでハートビートを送信します");
				sendHeartBeat(new Code1SendDto(gatewayInfo.getSequence()));
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		log.debug("ハートビートスレッドが終了しました。WebSocketを切断します。");

		// websocketの切断
		gatewaySendMessageService.close();

		log.debug("ハートビートスレッドが完了しました");
		isRunning.set(false);
	}

	public void sendHeartBeat(Code1SendDto dto) {
		gatewayHeartBeatCheckService.addWait();
		String json = objectMapper.writeValueAsString(dto);
		gatewaySendMessageService.sendMessage(json);
	}

	@PreDestroy
	public void stopHeartBeat() {
		if (heartBeatThread != null) {
			heartBeatThread.interrupt();
		}
	}
}
