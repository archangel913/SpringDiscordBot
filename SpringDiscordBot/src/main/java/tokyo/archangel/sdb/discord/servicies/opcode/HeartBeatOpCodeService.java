package tokyo.archangel.sdb.discord.servicies.opcode;

import java.io.IOException;
import java.util.Objects;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatOpCodeService extends AbstractOpCodeService {
	private static final String OP_CODE = "1";

	private String sequence;

	public HeartBeatOpCodeService(WebSocketSession session, String sequence) {
		super(session);
	}

	@Override
	public void exec() {
		String json = "{\"op\":" + OP_CODE + ",\"d\":";
		if (Objects.isNull(sequence)) {
			json += "null}";
		} else {
			json += sequence + "}";
		}

		try {
			session.sendMessage(new TextMessage(json));
			log.trace("Helloオペコードを送信しました");
			log.trace("送信内容：" + json);
		} catch (IOException e) {
			log.error("Helloオペコード送信中にエラーが発生しました。", e);
		}
	}
}
