package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatCheckService;

/**
 * gatewayからopcode10を受け取った時に実行するサービス
 */
@Component
@Slf4j
public class Opcode11Service implements OpcodeServiceInterface {
	private GatewayHeartBeatCheckService gatewayHeartBeatCheckService;

	public Opcode11Service(GatewayHeartBeatCheckService gatewayHeartBeatCheckService) {
		this.gatewayHeartBeatCheckService = gatewayHeartBeatCheckService;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("ハートビートを確認しました");
		gatewayHeartBeatCheckService.remove();
	}
}
