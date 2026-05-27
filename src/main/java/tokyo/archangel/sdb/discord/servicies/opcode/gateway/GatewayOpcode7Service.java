package tokyo.archangel.sdb.discord.servicies.opcode.gateway;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

/**
 * gatewayからopcode7を受け取った時に実行するサービス
 */
@Service
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
		HeartBeatService service = heartBeatServiceProvider
				.getHeartBeatService(sendMessageService.getSession());
		service.dispose();
		heartBeatServiceProvider.removeService(sendMessageService.getSession());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
