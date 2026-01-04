package tokyo.archangel.sdb.discord.servicies.opcode;

import java.io.IOException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.opcode.code1.Code1Dto;
import tools.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
@Slf4j
public class OpCode1Service extends AbstractOpCodeService {
	private final ObjectMapper objectMapper = new ObjectMapper();

	private Code1Dto dto;

	public OpCode1Service(Code1Dto dto, WebSocketSession session) {
		super(session);
		this.dto = dto;
	}

	@Override
	public void exec() {
		String json = objectMapper.writeValueAsString(dto);

		try {
			session.sendMessage(new TextMessage(json));
			log.trace("Helloオペコードを送信しました");
			log.trace("送信内容：" + json);
		} catch (IOException e) {
			log.error("Helloオペコード送信中にエラーが発生しました。", e);
		}
	}
}
