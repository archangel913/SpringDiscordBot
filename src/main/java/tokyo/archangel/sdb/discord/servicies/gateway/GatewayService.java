package tokyo.archangel.sdb.discord.servicies.gateway;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
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

	private OpcodeServiceFactory opcodeServiceFactory;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public GatewayService (OpcodeServiceFactory opcodeServiceFactory){
		this.opcodeServiceFactory = opcodeServiceFactory;
	}
	
	public void receive(String json) {
		log.trace("受信メッセージ: " + json);
		try {
			OpCodeReceiveBaseDto baseDto = objectMapper.readValue(json, OpCodeReceiveBaseDto.class);
			receive(baseDto);
		} catch (JacksonException e) {
			log.warn("jsonのパースに失敗しました。何も行いません。");
		} catch (Exception e) {
			log.warn("例外が発生しました。", e);
		}
	}

	public void receive(OpCodeReceiveBaseDto baseDto) {
		OpcodeServiceInterface service = opcodeServiceFactory.create(baseDto);
		if (service == null) {
			log.warn("サービスの取得に失敗しました。");
			return;
		}
		service.exec(baseDto);
	}
}
