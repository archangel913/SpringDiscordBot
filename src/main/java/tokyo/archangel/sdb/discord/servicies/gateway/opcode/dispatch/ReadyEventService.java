package tokyo.archangel.sdb.discord.servicies.gateway.opcode.dispatch;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.OpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.gateway.opcode.OpcodeSetterInterface;

@SuppressWarnings("deprecation")
@Service
@Slf4j
public class ReadyEventService implements OpcodeServiceInterface, OpcodeSetterInterface {
	private GatewayInfo gatewayInfo;

	private Long sequence;

	private ReadyDetail dto;

	public ReadyEventService(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void setSession(WebSocketSession session) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void setDto(OpCodeBaseDto dto) {
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof ReadyDetail) {
			this.sequence = ((Code0Dto) dto).getSequence();
			this.dto = (ReadyDetail) ((Code0Dto) dto).getDetail();
		} else {
			throw new ClassCastException("想定外の型です");
		}
	}

	@Override
	public void exec() {
		log.info("readyイベントを受け取りました");

		// 各種必要なボット情報を設定する
		gatewayInfo.setSequence(sequence);
		gatewayInfo.setReadyDetail(dto);
	}

}
