package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code1.Code1ReceiveDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tools.jackson.databind.ObjectMapper;

/**
 * gatewayからopcode1を受け取った時に実行するサービス
 */
@Slf4j
public class GatewayOpcode1Service implements GatewayOpcodeServiceInterface {
	private final ObjectMapper objectMapper = new ObjectMapper();

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private SendMessageService sendMessageService;

	public GatewayOpcode1Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		String json;
		if (dto instanceof Code1ReceiveDto) {
			json = objectMapper.writeValueAsString(new Code1SendDto(((Code1ReceiveDto) dto).getD()));
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		log.debug("discordのレスポンスでハートビートを送信します");
		HeartBeatService service = heartBeatServiceProvider.getHeartBeatService(sendMessageService.getSession());
		service.sendHeartBeat(json);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
