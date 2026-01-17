package tokyo.archangel.sdb.discord.servicies.gateway;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Properties;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class GatewayHeartBeatService {
	private Environment environment;

	private ApplicationProperties properties;

	private GatewayReconnectionService gatewayReconnectionService;

	private ReconnectionWaitThreads reconnectionWaitThreads;
	
	private GatewayInfo gatewayInfo; 

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Thread heartBeatThread;

	public GatewayHeartBeatService(Environment environment, ApplicationProperties properties,
			GatewayReconnectionService gatewayReconnectionService,
			ReconnectionWaitThreads reconnectionWaitThreads,
			GatewayInfo gatewayInfo) {
		this.environment = environment;
		this.properties = properties;
		this.gatewayReconnectionService = gatewayReconnectionService;
		this.reconnectionWaitThreads = reconnectionWaitThreads;
		this.gatewayInfo = gatewayInfo;
	}

	@Async
	public CompletableFuture<Void> run(int interval, WebSocketSession session) {
		if (heartBeatThread != null) {
			log.trace("すでに実行中のハートビートがあります");
			return CompletableFuture.completedFuture(null);
		}
		heartBeatThread = Thread.currentThread();
		log.debug("ボットを接続します");
		String json = objectMapper.writeValueAsString(generateCode2Dto());

		log.trace("Identifyオペコードを送信します");
		sendMessage(session, json);

		while (true) {
			log.debug("バックグラウンドでハートビートを送信します");
			sendHeartBeat(new Code1Dto(null, null, gatewayInfo.getSequence()), session);
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}

		// websocketの切断
		try {
			log.debug("websocketを切断します");
			session.close();
			log.info("discordから切断完了");
		} catch (IOException e) {
			log.error("discordの切断中にエラーが発生しました", e);
		}

		heartBeatThread = null;
		return CompletableFuture.completedFuture(null);
	}

	public void sendHeartBeat(Code1Dto dto, WebSocketSession session) {
		gatewayReconnectionService.setHeartbeatThread(heartBeatThread);
		gatewayReconnectionService.run();
		reconnectionWaitThreads.add(gatewayReconnectionService);

		String json = objectMapper.writeValueAsString(dto);
		sendMessage(session, json);
	}

	private void sendMessage(WebSocketSession session, String json) {
		try {
			session.sendMessage(new TextMessage(json));
			log.trace("送信内容：" + json);
		} catch (IOException e) {
			log.error("メッセージ送信中にエラーが発生しました。", e);
		}
	}

	private Code2Dto generateCode2Dto() {
		String token = properties.getBotToken();
		String os = environment.getProperty("os.name");
		String libName = environment.getProperty("spring.application.name");
		Properties properties = new Properties(os, libName, libName);

		// TODO インテントを実装する
		// 現状は最低限の1を設定
		Detail detail = new Detail(token, properties, 1);
		return new Code2Dto(detail, null, null);
	}
}
