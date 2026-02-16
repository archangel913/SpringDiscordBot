package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code9.Code9Dto;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatService;

/**
 * gatewayからopcode9を受け取った時に実行するサービス
 */
@Service
@Slf4j
public class Opcode9Service implements OpcodeServiceInterface {
	private GatewayHeartBeatService gatewayHeartBeatService;
	private GatewayInfo gatewayInfo;

	public Opcode9Service(GatewayHeartBeatService gatewayHeartBeatService, GatewayInfo gatewayInfo) {
		this.gatewayHeartBeatService = gatewayHeartBeatService;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.warn("opcode9を受信しました。");

		Code9Dto code9dto;
		if (dto instanceof Code9Dto) {
			code9dto = (Code9Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		if (code9dto.getD()) {
			// 再接続(opcode6)を行う
			log.info("再接続します。再接続用URLを使用します。");
			gatewayInfo.setReconnectMode(ReconnectMode.NORMAL);
			gatewayHeartBeatService.stopHeartBeat();
		} else {
			// 再接続(opcode2)を行う
			log.info("接続します。初期URLを使用します。");
			gatewayInfo.setReconnectMode(ReconnectMode.HARD);
			gatewayHeartBeatService.stopHeartBeat();
		}
	}
}
