package tokyo.archangel.sdb.discord.servicies.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.OpcodeServiceFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * 
 */
@Service
@Slf4j
public class GatewayService {
	@Autowired
	private OpcodeServiceFactory codeServiceFactory;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public void receive(String json, WebSocketSession session) {
		log.trace("受信メッセージ: " + json);
		try {
			
			if (objectMapper.readTree(json).get("op").asInt() == 0
					&& !objectMapper.readTree(json).get("t").asString().equals("READY")) {
				log.warn("パスします");
				return;
			}
			OpCodeBaseDto baseDto = objectMapper.readValue(json, OpCodeBaseDto.class);
			codeServiceFactory.create(baseDto, session).exec();
		} catch (JacksonException e) {
			log.warn("jsonのパースに失敗しました。何も行いません。", e);
		}
	}
}
