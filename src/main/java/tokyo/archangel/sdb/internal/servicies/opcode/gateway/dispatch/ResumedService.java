package tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

@Slf4j
public class ResumedService implements GatewayOpcodeServiceInterface {
	private GatewayInfo gatewayInfo;

	public ResumedService(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.info("Resumedイベントを受け取りました");
		// 再接続に成功したため、連続失敗回数をリセットする
		gatewayInfo.setConnectionFailCount(0);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		// 必要ないためから実装
	}

}
