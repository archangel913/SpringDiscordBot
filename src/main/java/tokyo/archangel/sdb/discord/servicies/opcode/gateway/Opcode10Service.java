package tokyo.archangel.sdb.discord.servicies.opcode.gateway;

import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code10.Code10Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Code2Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code2.Properties;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code6.Code6Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code6.Code6Dto;
import tokyo.archangel.sdb.discord.enumeration.Intent;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.opcode.OpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tools.jackson.databind.ObjectMapper;

/**
 * gatewayからopcode10を受け取った時に実行するサービス
 */
@Service
@Slf4j
public class Opcode10Service implements OpcodeServiceInterface {
	private HeartBeatServiceProvider heartBeatServiceProvider;

	private SendMessageService sendMessageService;

	private GatewayInfo gatewayInfo;

	private Environment environment;

	private ApplicationProperties properties;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public Opcode10Service(HeartBeatServiceProvider heartBeatServiceProvider, GatewayInfo gatewayInfo,
			Environment environment, ApplicationProperties properties) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.gatewayInfo = gatewayInfo;
		this.environment = environment;
		this.properties = properties;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		int interval;
		if (dto instanceof Code10Dto) {
			interval = ((Code10Dto) dto).getDetail().getHeartbeatInterval();
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		String json;
		log.debug("ボットを接続します");
		if (gatewayInfo.getReconnectMode() == ReconnectMode.NORMAL) {
			json = objectMapper.writeValueAsString(generateCode6Dto());
			log.debug("Resumeオペコードを送信します");
		} else {
			json = objectMapper.writeValueAsString(generateCode2Dto());
			log.debug("Identifyオペコードを送信します");
		}

		sendMessageService.sendMessage(json);
		HeartBeatService heartBeatService = heartBeatServiceProvider
				.getHeartBeatService(sendMessageService.getSession());
		heartBeatService.setSendMessageService(sendMessageService);
		heartBeatService.setSendOpcode(Code1SendDto.class.getName());
		heartBeatService.exec(interval);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

	private Code2Dto generateCode2Dto() {
		String token = properties.getBotToken();
		String os = environment.getProperty("os.name");
		String libName = environment.getProperty("spring.application.name");
		Properties property = new Properties(os, libName, libName);

		List<Intent> intents = properties.getIntents();
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
