package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1ReceiveDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatService;

/**
 * gatewayからopcode1を受け取った時に実行するサービス
 */
@Component
@Slf4j
public class Opcode1Service implements OpcodeServiceInterface {
	private GatewayHeartBeatService gatewayHeartBeatService;

	public Opcode1Service(GatewayHeartBeatService gatewayHeartBeatService) {
		this.gatewayHeartBeatService = gatewayHeartBeatService;
	}

	@Override
	public void exec(WebSocketSession session, OpCodeReceiveBaseDto dto) {
		Code1SendDto send;
		if (dto instanceof Code1ReceiveDto) {
			send = new Code1SendDto(((Code1ReceiveDto)dto).getD());
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		log.debug("discordのレスポンスでハートビートを送信します");
		gatewayHeartBeatService.sendHeartBeat(send, session);
	}
}
