package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code9.Code9Dto;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;

class GatewayOpcode9ServiceTest {

	private GatewayInfo gatewayInfo;

	private GatewayOpcode9Service service;

	@BeforeEach
	void setUp() {
		gatewayInfo = new GatewayInfo();
		service = new GatewayOpcode9Service(mock(HeartBeatServiceProvider.class), gatewayInfo);
	}

	@DisplayName("再接続可能な場合はresumeフラグを立てる")
	@Test
	void exec_setsResumeFlagWhenResumable() {
		gatewayInfo.setResume(false);

		service.exec(new Code9Dto(true, null, null));

		assertThat(gatewayInfo.isResume()).isTrue();
	}

	@DisplayName("再接続不可能な場合はresumeフラグを下ろす")
	@Test
	void exec_clearsResumeFlagWhenNotResumable() {
		gatewayInfo.setResume(true);

		service.exec(new Code9Dto(false, null, null));

		assertThat(gatewayInfo.isResume()).isFalse();
	}

	@DisplayName("Code9Dto以外を受信した場合は何もしない")
	@Test
	void exec_doesNothingWhenDtoIsNotCode9Dto() {
		gatewayInfo.setResume(true);

		service.exec(mock(OpCodeReceiveBaseDto.class));

		assertThat(gatewayInfo.isResume()).isTrue();
	}
}
