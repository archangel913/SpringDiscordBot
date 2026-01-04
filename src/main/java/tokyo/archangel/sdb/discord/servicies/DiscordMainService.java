package tokyo.archangel.sdb.discord.servicies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.discord.dto.opcode.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.servicies.opcode.OpCodeServiceFactory;
import tools.jackson.databind.ObjectMapper;

@Service
public class DiscordMainService {
	@Autowired
	private OpCodeServiceFactory codeServiceFactory;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public void receive(String json, WebSocketSession session) {
		OpCodeBaseDto baseDto = objectMapper.readValue(json, OpCodeBaseDto.class);
		codeServiceFactory.create(baseDto, session).exec();
	}
}
