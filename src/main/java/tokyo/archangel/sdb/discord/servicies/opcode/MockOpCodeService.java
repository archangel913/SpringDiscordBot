package tokyo.archangel.sdb.discord.servicies.opcode;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class MockOpCodeService  extends AbstractOpCodeService{
	public MockOpCodeService(WebSocketSession session) {
		super(session);
	}

	@Override
	public void exec() {
		// 何もしない
		log.debug("モックサービスが呼ばれました");
	}

}
