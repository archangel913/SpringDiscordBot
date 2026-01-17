package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatService;

/**
 * gatewayからopcode1を受け取った時に実行するサービス
 */
@SuppressWarnings("deprecation")
@Component
@Slf4j
public class Opcode1Service implements OpcodeServiceInterface, OpcodeSetterInterface {
	private GatewayHeartBeatService gatewayHeartBeatService;

	private WebSocketSession session;

	private Code1Dto dto;

	public Opcode1Service(GatewayHeartBeatService gatewayHeartBeatService) {
		this.gatewayHeartBeatService = gatewayHeartBeatService;
	}

	@Override
	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	@Override
	public void setDto(OpCodeBaseDto dto) {
		if (dto instanceof Code1Dto) {
			this.dto = (Code1Dto) dto;
		} else {
			throw new ClassCastException("想定外の型です");
		}
	}

	@Override
	public void exec() {
		if (!validate()) {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		log.debug("discordのレスポンスでハートビートを送信します");
		gatewayHeartBeatService.sendHeartBeat(dto, session);
	}

	private boolean validate() {
		return Objects.nonNull(session) && Objects.nonNull(dto);
	}
}
