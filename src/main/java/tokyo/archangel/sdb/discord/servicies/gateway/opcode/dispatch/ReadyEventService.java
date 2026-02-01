package tokyo.archangel.sdb.discord.servicies.gateway.opcode.dispatch;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.OpcodeServiceInterface;

@Service
@Slf4j
public class ReadyEventService implements OpcodeServiceInterface {
	private GatewayInfo gatewayInfo;

	public ReadyEventService(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void exec(WebSocketSession session, OpCodeReceiveBaseDto dto) {
		log.info("readyイベントを受け取りました");
		Long sequence;
		ReadyDetail readyDetail;
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof ReadyDetail) {
			Code0Dto code0Dto = (Code0Dto) dto;
			sequence = code0Dto.getSequence();
			readyDetail = (ReadyDetail) ((Code0Dto) dto).getDetail();
		} else {
			log.warn("想定外の型のため処理を実行しません");
			return;
		}

		// 各種必要なボット情報を設定する
		gatewayInfo.setSequence(sequence);
		gatewayInfo.setReadyDetail(readyDetail);
	}

}
