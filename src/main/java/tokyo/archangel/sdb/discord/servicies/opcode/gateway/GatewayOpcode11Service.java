package tokyo.archangel.sdb.discord.servicies.opcode.gateway;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

/**
 * gatewayからopcode11を受け取った時に実行するサービス
 */
@Slf4j
public class GatewayOpcode11Service implements GatewayOpcodeServiceInterface {
	private HeartBeatServiceProvider heartBeatServiceProvider;

	private SendMessageService sendMessageService;

	public GatewayOpcode11Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("ハートビートを確認しました");
		HeartBeatService service = heartBeatServiceProvider.getHeartBeatService(sendMessageService.getSession());
		service.receiveAck();
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
