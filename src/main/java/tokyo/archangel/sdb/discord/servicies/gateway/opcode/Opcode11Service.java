package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code11.Code11Dto;
import tokyo.archangel.sdb.discord.servicies.gateway.ReconnectionWaitThreads;

/**
 * gatewayからopcode10を受け取った時に実行するサービス
 */
@SuppressWarnings("deprecation")
@Component
@Slf4j
public class Opcode11Service implements OpcodeServiceInterface, OpcodeSetterInterface {
	private ReconnectionWaitThreads reconnectionWaitThreads;

	private GatewayInfo gatewayInfo;

	private Code11Dto dto;

	public Opcode11Service(ReconnectionWaitThreads reconnectionWaitThreads, GatewayInfo gatewayInfo) {
		this.reconnectionWaitThreads = reconnectionWaitThreads;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void setSession(WebSocketSession session) {
	}

	@Override
	public void setDto(OpCodeBaseDto dto) {
		if (dto instanceof Code11Dto) {
			this.dto = (Code11Dto) dto;
		} else {
			throw new ClassCastException("想定外の型です");
		}
	}

	@Override
	public void exec() {
		log.debug("ハートビートを確認しました");
		reconnectionWaitThreads.remove().interrupt();

		gatewayInfo.setSequence(dto.getSequence());
	}
}
