package tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code9.Code9Dto;

class ReadyEventServiceTest {

	private GatewayInfo gatewayInfo;

	private ReadyEventService service;

	@BeforeEach
	void setUp() {
		gatewayInfo = new GatewayInfo();
		gatewayInfo.setConnectionFailCount(3);
		service = new ReadyEventService(gatewayInfo);
	}

	@DisplayName("READYイベントを受信するとReadyDetailを保存し失敗回数をリセットする")
	@Test
	void exec_savesReadyDetailAndResetsFailCountOnReadyEvent() {
		ReadyDetail readyDetail = mock(ReadyDetail.class);
		Code0Dto dto = new Code0Dto(null, readyDetail, 10L);

		service.exec(dto);

		assertThat(gatewayInfo.getReadyDetail()).isSameAs(readyDetail);
		assertThat(gatewayInfo.getConnectionFailCount()).isZero();
	}

	@DisplayName("詳細がReadyDetailでない場合は何もしない")
	@Test
	void exec_doesNothingWhenDetailIsNotReadyDetail() {
		EventDetailBase otherDetail = mock(EventDetailBase.class);
		Code0Dto dto = new Code0Dto(null, otherDetail, 10L);

		service.exec(dto);

		assertThat(gatewayInfo.getReadyDetail()).isNull();
		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(3);
	}

	@DisplayName("Code0Dto以外を受信した場合は何もしない")
	@Test
	void exec_doesNothingWhenDtoIsNotCode0Dto() {
		Code9Dto dto = new Code9Dto(true, null, 10L);

		service.exec(dto);

		assertThat(gatewayInfo.getReadyDetail()).isNull();
		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(3);
	}
}
