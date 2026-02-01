package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatCheckService;

@Service
@Slf4j
public class Opcode7Service implements OpcodeServiceInterface {
	private GatewayHeartBeatCheckService gatewayHeartBeatCheckService;
	private GatewayInfo gatewayInfo;
	
	public Opcode7Service(GatewayHeartBeatCheckService gatewayHeartBeatCheckService, GatewayInfo gatewayInfo) {
		this.gatewayHeartBeatCheckService = gatewayHeartBeatCheckService;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(WebSocketSession session, OpCodeReceiveBaseDto dto) {
		log.info("再接続します。");
		gatewayInfo.setReconnectMode(ReconnectMode.NORMAL);
		gatewayHeartBeatCheckService.stopHeartBeatCheak();
	}
}
