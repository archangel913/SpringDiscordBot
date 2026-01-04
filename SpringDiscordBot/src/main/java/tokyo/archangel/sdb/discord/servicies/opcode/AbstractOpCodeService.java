package tokyo.archangel.sdb.discord.servicies.opcode;

import org.springframework.web.socket.WebSocketSession;

public abstract class AbstractOpCodeService {
	protected WebSocketSession session;

	public AbstractOpCodeService(WebSocketSession session) {
		this.session = session;
	}

	/**
	 * 各オペコードの処理
	 */
	public abstract void exec();
}
