package tokyo.archangel.sdb.discord.servicies.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.OpcodeServiceFactory;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.OpcodeServiceInterface;
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
			OpCodeBaseDto baseDto = objectMapper.readValue(json, OpCodeBaseDto.class);
			OpcodeServiceInterface service = codeServiceFactory.create(baseDto);
			if(service == null) {
				log.warn("サービスの取得に失敗しました。");
				return;
			}
			service.exec(session, baseDto);
		} catch (JacksonException e) {
			log.warn("jsonのパースに失敗しました。何も行いません。", e);
		} catch (Exception e) {
			log.warn("例外が発生しました。", e);
		}
	}
}
