package tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;

class ResumedServiceTest {

	@DisplayName("RESUMEDイベントを受信すると失敗回数をリセットする")
	@Test
	void exec_resetsFailCountOnResumedEvent() {
		GatewayInfo gatewayInfo = new GatewayInfo();
		gatewayInfo.setConnectionFailCount(4);
		ResumedService service = new ResumedService(gatewayInfo);

		service.exec(mock(OpCodeReceiveBaseDto.class));

		assertThat(gatewayInfo.getConnectionFailCount()).isZero();
	}

	@DisplayName("失敗回数が既に0の場合も0のままである")
	@Test
	void exec_keepsFailCountAtZeroWhenAlreadyZero() {
		GatewayInfo gatewayInfo = new GatewayInfo();
		ResumedService service = new ResumedService(gatewayInfo);

		service.exec(mock(OpCodeReceiveBaseDto.class));

		assertThat(gatewayInfo.getConnectionFailCount()).isZero();
	}
}
