package tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class ReadyEventService implements GatewayOpcodeServiceInterface {
	private GatewayInfo gatewayInfo;

	public ReadyEventService(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.info("readyイベントを受け取りました");
		ReadyDetail readyDetail;
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof ReadyDetail) {
			readyDetail = (ReadyDetail) ((Code0Dto) dto).getDetail();
		} else {
			log.warn("想定外の型のため処理を実行しません");
			return;
		}

		// 各種必要なボット情報を設定する
		gatewayInfo.setReadyDetail(readyDetail);
		gatewayInfo.setConnectionFailCount(0);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
	}

}
