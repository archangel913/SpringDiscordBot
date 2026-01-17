package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code10.Code10Dto;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatService;

/**
 * gatewayからopcode10を受け取った時に実行するサービス
 */
@SuppressWarnings("deprecation")
@Component
@Slf4j
public class Opcode10Service implements OpcodeServiceInterface, OpcodeSetterInterface {
	private GatewayHeartBeatService gatewayHeartBeatService;

	private WebSocketSession session;

	private Code10Dto dto;

	public Opcode10Service(GatewayHeartBeatService gatewayHeartBeatService) {
		this.gatewayHeartBeatService = gatewayHeartBeatService;
	}

	@Override
	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	@Override
	public void setDto(OpCodeBaseDto dto) {
		if (dto instanceof Code10Dto) {
			this.dto = (Code10Dto) dto;
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
		gatewayHeartBeatService.run(dto.getDetail().getHeartbeatInterval(), session);
	}

	private boolean validate() {
		return Objects.nonNull(session) && Objects.nonNull(dto);
	}
}
