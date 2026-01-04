package tokyo.archangel.sdb.discord.servicies;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.servicies.opcode.AbstractOpCodeService;
import tokyo.archangel.sdb.discord.servicies.opcode.DiscordHeartBeatOpCodeService;
import tokyo.archangel.sdb.discord.servicies.opcode.HeartBeatOpCodeService;
import tokyo.archangel.sdb.discord.servicies.opcode.MockOpCodeService;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class DiscordMainService {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public void receive(String json, WebSocketSession session) {
		AbstractOpCodeService service;
		try {
			service = generateService(json, session);
			service.exec();
		} catch (Exception e) {
			log.error("jsonの処理に失敗しました", e);
		}
	}

	private AbstractOpCodeService generateService(String json, WebSocketSession session) throws Exception {
		String opCode;
		try {
			opCode = objectMapper.readTree(json).get("op").asString();
		} catch (Exception e) {
			throw e;
		}

		if (opCode.isBlank()) {
			throw new Exception("opCodeが空です");
		}

		// TODO ここのファクトリーメソッドBean管理にしたい
		switch (opCode) {
		case "1":
			return new HeartBeatOpCodeService(session, null);
		case "10":
			int interval = objectMapper.readTree(json).get("d").get("heartbeat_interval").asInt();
			return new DiscordHeartBeatOpCodeService(session, interval);
		case "11":
			log.debug("ハートビート11を確認しました");
			return new MockOpCodeService(session);
		default:
			throw new Exception("opCodeが不正です");
		}
	}
}
