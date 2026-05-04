package tokyo.archangel.sdb.discord.servicies.voice;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.opcode.OpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcodeServiceFactory;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;

/**
 * 
 */
@Service
@Slf4j
public class VoiceService {

	private VoiceOpcodeServiceFactory opcodeServiceFactory;

	private GatewayInfo gatewayInfo;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public VoiceService(@Lazy VoiceOpcodeServiceFactory opcodeServiceFactory, GatewayInfo gatewayInfo) {
		this.opcodeServiceFactory = opcodeServiceFactory;
		this.gatewayInfo = gatewayInfo;
	}

	public void receive(String json, SendMessageService service) {
		log.trace("受信メッセージ: " + json);
		try {
			// シーケンスだけはあらかじめ取っておく
			JsonNode jsonSeq = objectMapper.readTree(json).get("s");
			if (jsonSeq != null && !jsonSeq.isNull()) {
				try {
					long seq = jsonSeq.asLong();
					gatewayInfo.setSequence(seq);
				} catch (JsonNodeException e) {
					log.debug("シーケンスの取得に失敗しました");
				}
			}

			OpCodeReceiveBaseDto baseDto = objectMapper.readValue(json, OpCodeReceiveBaseDto.class);
			receive(baseDto, service);
		} catch (JacksonException e) {
			log.warn("jsonのパースに失敗しました。何も行いません。");
		} catch (Exception e) {
			log.warn("例外が発生しました。", e);
		}
	}

	public void receive(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		OpcodeServiceInterface service = opcodeServiceFactory.create(baseDto, sendMessageService);
		if (service == null) {
			log.warn("サービスの取得に失敗しました。");
			return;
		}
		service.exec(baseDto);
	}
}
