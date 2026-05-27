package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.util.Random;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code3.Code3ReceiveDto;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tools.jackson.databind.ObjectMapper;

/**
 * ハートビート送信を管理するクラス
 */
@Service
@Scope("prototype")
@Slf4j
public class HeartBeatServiceImpl implements HeartBeatService {

	private HeartBeatCheckService HeartBeatCheckService;

	private SendMessageService sendMessageService;

	private GatewayInfo gatewayInfo;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Random random = new Random();

	private ServiceThreadStatus status;

	private Thread currentThread;

	private VoiceChannelInfo voiceChannelInfo;

	public HeartBeatServiceImpl(HeartBeatCheckService HeartBeatCheckService,
			GatewayInfo gatewayInfo) {
		this.HeartBeatCheckService = HeartBeatCheckService;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void setVoiceChannelInfo(VoiceChannelInfo voiceChannelInfo) {
		this.voiceChannelInfo = voiceChannelInfo;
	}

	@Override
	public void setSendMessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

	@Async
	@Override
	public synchronized void exec(int interval) {
		if (status == ServiceThreadStatus.ACTIVE) {
			log.debug("ハートビートスレッドがすでに起動しています");
			return;
		}
		status = ServiceThreadStatus.ACTIVE;
		currentThread = Thread.currentThread();
		currentThread.setName("sendHeartBeat");

		HeartBeatCheckService.setHeartbeatService(this);
		HeartBeatCheckService.exec();
		log.debug("ハートビートスレッドを起動します。");

		try {
			Thread.sleep((long) (interval * random.nextDouble()));
			while (status == ServiceThreadStatus.ACTIVE) {
				log.debug("バックグラウンドでハートビートを送信します");
				String json = generateJson();
				if (json != null) {
					sendHeartBeat(json);
				}
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			HeartBeatCheckService.dispose();
		}
	}

	@Override
	public void receiveAck() {
		HeartBeatCheckService.remove();
	}

	@Override
	public void sendHeartBeat(String json) {
		HeartBeatCheckService.addWait();
		sendMessageService.sendMessage(json);
	}

	@PreDestroy
	@Override
	public void dispose() {
		HeartBeatCheckService.dispose();
		if (status != ServiceThreadStatus.ACTIVE) {
			return;
		}
		status = ServiceThreadStatus.TERMINATING;
		currentThread.interrupt();
		log.debug("ハートビートスレッドを終了します。");
		// websocketの切断
		sendMessageService.dispose();
		log.debug("ハートビートスレッドが完了しました");
		status = ServiceThreadStatus.TERMINATED;
	}

	private String generateJson() {
		if (voiceChannelInfo == null) {
			// ゲートウェイのハートビートの場合
			return objectMapper.writeValueAsString(new Code1SendDto(gatewayInfo.getSequence()));
		} else {
			// ボイスのハートビートの場合
			// 使い捨ての値を取得
			long nonce = random.nextLong();
			return objectMapper.writeValueAsString(new Code3ReceiveDto(nonce));
		}
	}
}
