package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;

public interface OpcodeServiceInterface {
	/** サービスの処理を実行 */
	public void exec(WebSocketSession session, OpCodeBaseDto dto);
}
