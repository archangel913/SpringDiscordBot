package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code10.Code10Dto;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatService;

/**
 * gatewayからopcode10を受け取った時に実行するサービス
 */
@Component
@Slf4j
public class Opcode10Service implements OpcodeServiceInterface {
	private GatewayHeartBeatService gatewayHeartBeatService;

	public Opcode10Service(GatewayHeartBeatService gatewayHeartBeatService) {
		this.gatewayHeartBeatService = gatewayHeartBeatService;
	}

	@Override
	public void exec(WebSocketSession session, OpCodeBaseDto dto) {
		int interval;
		if (dto instanceof Code10Dto) {
			interval = ((Code10Dto) dto).getDetail().getHeartbeatInterval();
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}
		gatewayHeartBeatService.run(interval, session);
	}
}
