package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code9.Code9Dto;
import tokyo.archangel.sdb.internal.enumeration.ReconnectMode;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

/**
 * gatewayからopcode9を受け取った時に実行するサービス
 */
@Slf4j
public class GatewayOpcode9Service implements GatewayOpcodeServiceInterface {
	private HeartBeatServiceProvider heartBeatServiceProvider;

	private SendMessageService sendMessageService;

	private GatewayInfo gatewayInfo;

	public GatewayOpcode9Service(HeartBeatServiceProvider heartBeatServiceProvider, GatewayInfo gatewayInfo) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
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
		} else {
			// 再接続(opcode2)を行う
			log.info("接続します。初期URLを使用します。");
			gatewayInfo.setReconnectMode(ReconnectMode.HARD);
		}

		heartBeatServiceProvider.removeService(sendMessageService.getSession());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
