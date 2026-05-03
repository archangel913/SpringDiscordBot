package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.util.Random;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
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

	private String opcodeClassName;

	public HeartBeatServiceImpl(HeartBeatCheckService HeartBeatCheckService,
			GatewayInfo gatewayInfo) {
		this.HeartBeatCheckService = HeartBeatCheckService;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void setSendOpcode(String opcodeClassName) {
		this.opcodeClassName = opcodeClassName;
	}

	@Override
	public void setSendMessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

	@Async
	@Override
	public synchronized void exec(int interval) {
		if(status == ServiceThreadStatus.ACTIVE) {
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
				OpCodeSendBaseDto dto = generateDto();
				if (dto != null) {
					sendHeartBeat(dto);
				}
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			HeartBeatCheckService.stopHeartBeatCheak();
		}
	}

	@Override
	public void receiveAck() {
		HeartBeatCheckService.remove();
	}

	@Override
	public void sendHeartBeat(OpCodeSendBaseDto dto) {
		HeartBeatCheckService.addWait();
		String json = objectMapper.writeValueAsString(dto);
		sendMessageService.sendMessage(json);
	}

	@Override
	public void stopHeartBeat() {
		if (status != ServiceThreadStatus.ACTIVE) {
			return;
		}
		status = ServiceThreadStatus.TERMINATING;
		currentThread.interrupt();
		log.debug("ハートビートスレッドを終了します。WebSocketを切断します。");
		// websocketの切断
		sendMessageService.dispose();
		log.debug("ハートビートスレッドが完了しました");
		status = ServiceThreadStatus.TERMINATED;
	}

	@PreDestroy
	public void dispose() {
		HeartBeatCheckService.stopHeartBeatCheak();
	}

	private OpCodeSendBaseDto generateDto() {
		OpCodeSendBaseDto dto = null;
		if (opcodeClassName == Code1SendDto.class.getName()) {
			// ゲートウェイのハートビートの場合
			dto = new Code1SendDto(gatewayInfo.getSequence());
		} else if (opcodeClassName == Code1SendDto.class.getName()) {
			// ボイスのハートビートの場合
			dto = new Code1SendDto(gatewayInfo.getSequence());
		} else {
			log.warn("適切なOpCodeが設定されていないため、送信しません");
		}
		return dto;
	}
}
