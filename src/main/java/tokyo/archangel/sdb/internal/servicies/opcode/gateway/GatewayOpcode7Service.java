package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.enumeration.ReconnectMode;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

/**
 * gatewayからopcode7を受け取った時に実行するサービス
 */
@Slf4j
public class GatewayOpcode7Service implements GatewayOpcodeServiceInterface {
	private HeartBeatServiceProvider heartBeatServiceProvider;

	private SendMessageService sendMessageService;

	private GatewayInfo gatewayInfo;

	public GatewayOpcode7Service(HeartBeatServiceProvider heartBeatServiceProvider, GatewayInfo gatewayInfo) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.info("再接続します。");
		gatewayInfo.setReconnectMode(ReconnectMode.NORMAL);
		heartBeatServiceProvider.removeService(sendMessageService.getSession());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
