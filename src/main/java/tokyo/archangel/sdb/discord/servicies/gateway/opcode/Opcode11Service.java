package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.servicies.gateway.ReconnectionWaitThreads;

/**
 * gatewayからopcode10を受け取った時に実行するサービス
 */
@Component
@Slf4j
public class Opcode11Service implements OpcodeServiceInterface {
	private ReconnectionWaitThreads reconnectionWaitThreads;

	private GatewayInfo gatewayInfo;

	public Opcode11Service(ReconnectionWaitThreads reconnectionWaitThreads, GatewayInfo gatewayInfo) {
		this.reconnectionWaitThreads = reconnectionWaitThreads;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(WebSocketSession session, OpCodeBaseDto dto) {
		log.debug("ハートビートを確認しました");
		reconnectionWaitThreads.remove().interrupt();

		gatewayInfo.setSequence(dto.getSequence());
	}
}
