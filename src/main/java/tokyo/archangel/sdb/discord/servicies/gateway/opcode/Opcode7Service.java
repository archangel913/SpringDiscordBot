package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;

/**
 * gatewayからopcode7を受け取った時に実行するサービス
 */
@Component
@Slf4j
public class Opcode7Service implements OpcodeServiceInterface {
	public Opcode7Service() {
	}

	@Override
	public void exec(WebSocketSession session, OpCodeBaseDto dto) {
		// log.warn("必要なデータが揃っていないため処理を実行しません");

		log.debug("OPCODE7");
	}
}
