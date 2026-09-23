package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code7.Code7Dto;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;

class GatewayOpcode7ServiceTest {

	@DisplayName("再接続要求を受信するとresumeフラグを立てる")
	@Test
	void exec_setsResumeFlagOnReconnectRequest() {
		GatewayInfo gatewayInfo = new GatewayInfo();
		gatewayInfo.setResume(false);
		GatewayOpcode7Service service = new GatewayOpcode7Service(mock(HeartBeatServiceProvider.class), gatewayInfo);

		service.exec(new Code7Dto(null, null));

		assertThat(gatewayInfo.isResume()).isTrue();
	}

	@DisplayName("resumeフラグが既に立っている場合も維持される")
	@Test
	void exec_keepsResumeFlagTrueWhenAlreadyTrue() {
		GatewayInfo gatewayInfo = new GatewayInfo();
		gatewayInfo.setResume(true);
		GatewayOpcode7Service service = new GatewayOpcode7Service(mock(HeartBeatServiceProvider.class), gatewayInfo);

		service.exec(new Code7Dto(null, null));

		assertThat(gatewayInfo.isResume()).isTrue();
	}
}
