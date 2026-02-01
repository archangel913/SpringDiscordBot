package tokyo.archangel.sdb.discord.servicies.gateway;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Code2Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Properties;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code6.Code6Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code6.Code6Dto;
import tokyo.archangel.sdb.discord.enumeration.Intent;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class GatewayHeartBeatService {
	private Environment environment;

	private ApplicationProperties properties;

	private GatewayHeartBeatCheckService gatewayHeartBeatCheckService;

	private GatewayInfo gatewayInfo;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Random random = new Random();

	public GatewayHeartBeatService(Environment environment, ApplicationProperties properties,
			GatewayHeartBeatCheckService gatewayHeartBeatCheckService,
			GatewayInfo gatewayInfo) {
		this.environment = environment;
		this.properties = properties;
		this.gatewayHeartBeatCheckService = gatewayHeartBeatCheckService;
		this.gatewayInfo = gatewayInfo;
	}

	@Async
	public CompletableFuture<Void> run(int interval, WebSocketSession session) {
		gatewayHeartBeatCheckService.setHeartbeatThread(Thread.currentThread());
		gatewayHeartBeatCheckService.run();

		String json;
		log.debug("ボットを接続します");
		if (gatewayInfo.getReconnectMode() == ReconnectMode.NORMAL) {
			json = objectMapper.writeValueAsString(generateCode6Dto());
			log.debug("Resumeオペコードを送信します");
		} else {
			json = objectMapper.writeValueAsString(generateCode2Dto());
			log.debug("Identifyオペコードを送信します");
		}

		sendMessage(session, json);

		try {
			Thread.sleep((long) (interval * random.nextDouble()));
			while (true) {
				log.debug("バックグラウンドでハートビートを送信します");
				sendHeartBeat(new Code1SendDto(gatewayInfo.getSequence()), session);
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		log.info("ハートビートスレッドが終了しました。WebSocketを切断します。");

		// websocketの切断
		try {
			log.debug("websocketを切断します");
			session.close();
			log.info("discordから切断完了。");
		} catch (IOException e) {
			log.error("discordの切断中にエラーが発生しました。", e);
		}

		log.debug("ハートビートスレッドが完了しました");
		return CompletableFuture.completedFuture(null);
	}

	public void sendHeartBeat(Code1SendDto dto, WebSocketSession session) {
		gatewayHeartBeatCheckService.addWait();
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
		Properties property = new Properties(os, libName, libName);

		List<Intent> intents =  properties.getIntents();
		int intent = Intent.buildIntent(intents);
		Code2Detail detail = new Code2Detail(token, property, intent);
		return new Code2Dto(detail);
	}

	private Code6Dto generateCode6Dto() {
		ReadyDetail readyDetail = gatewayInfo.getReadyDetail();
		String sessionId = readyDetail.getSessionId();
		long seq = gatewayInfo.getSequence();

		Code6Detail datail = new Code6Detail(properties.getBotToken(), sessionId, seq);
		Code6Dto dto = new Code6Dto(datail);
		return dto;
	}
}
