package tokyo.archangel.sdb.internal.servicies.gateway;

import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcodeServiceFactory;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;

@Slf4j
public class GatewayService {

	private GatewayOpcodeServiceFactory opcodeServiceFactory;

	private GatewayInfo gatewayInfo;

	private HeartBeatServiceProvider heartbeatServiceProvider;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public GatewayService(GatewayOpcodeServiceFactory opcodeServiceFactory, GatewayInfo gatewayInfo,
			HeartBeatServiceProvider heartbeatServiceProvider) {
		this.opcodeServiceFactory = opcodeServiceFactory;
		this.gatewayInfo = gatewayInfo;
		this.heartbeatServiceProvider = heartbeatServiceProvider;
	}

	public void receive(String json, SendMessageService service) {
		log.trace("受信メッセージ: " + json);
		try {
			// シーケンスだけはあらかじめ取っておく
			JsonNode jsonSeq = objectMapper.readTree(json).get("s");
			if (jsonSeq != null && !jsonSeq.isNull()) {
				try {
					long seq = jsonSeq.asLong();
					gatewayInfo.setSequence(seq);
				} catch (JsonNodeException e) {
					log.debug("シーケンスの取得に失敗しました");
				}
			}

			OpCodeReceiveBaseDto baseDto = objectMapper.readValue(json, OpCodeReceiveBaseDto.class);
			receive(baseDto, service);
		} catch (JacksonException e) {
			log.warn("jsonのパースに失敗しました。何も行いません。");
		} catch (Exception e) {
			log.warn("例外が発生しました。", e);
		}
	}

	public void receive(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		GatewayOpcodeServiceInterface service = opcodeServiceFactory.create(baseDto, sendMessageService);
		if (service == null) {
			log.warn("サービスの取得に失敗しました。");
			return;
		}
		service.exec(baseDto);
	}

	public void close(WebSocketSession session) {
		heartbeatServiceProvider.removeService(session);
	}
}
