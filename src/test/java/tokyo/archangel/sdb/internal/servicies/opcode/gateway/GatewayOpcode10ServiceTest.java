package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.config.ApplicationProperties;
import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code10.Code10Detail;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code10.Code10Dto;
import tokyo.archangel.sdb.internal.enumeration.Intent;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

@ExtendWith(MockitoExtension.class)
class GatewayOpcode10ServiceTest {

	@Mock
	private HeartBeatServiceProvider heartBeatServiceProvider;

	@Mock
	private Environment environment;

	@Mock
	private SendMessageService sendMessageService;

	@Mock
	private WebSocketSession session;

	@Mock
	private HeartBeatService heartBeatService;

	private GatewayInfo gatewayInfo;

	private ApplicationProperties properties;

	private GatewayOpcode10Service service;

	@BeforeEach
	void setUp() {
		gatewayInfo = new GatewayInfo();
		properties = new ApplicationProperties();
		properties.setBotToken("bot-token");
		properties.setIntents(List.of(Intent.GUILDS));

		service = new GatewayOpcode10Service(heartBeatServiceProvider, gatewayInfo, environment, properties);
		service.setSendSessageService(sendMessageService);
	}

	@DisplayName("resumeフラグがfalseの場合はIdentifyを送信しresumeフラグを立てる")
	@Test
	void exec_sendsIdentifyAndSetsResumeFlagWhenResumeIsFalse() {
		gatewayInfo.setResume(false);
		when(sendMessageService.getSession()).thenReturn(session);
		when(heartBeatServiceProvider.getHeartBeatService(session)).thenReturn(heartBeatService);
		when(environment.getProperty("os.name")).thenReturn("TestOS");
		when(environment.getProperty("spring.application.name")).thenReturn("TestBot");

		Code10Dto dto = new Code10Dto(new Code10Detail(45000, null), null, null);
		service.exec(dto);

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(sendMessageService).sendMessage(captor.capture());
		assertThat(captor.getValue()).contains("\"op\":2");

		verify(heartBeatService).setSendMessageService(session);
		verify(heartBeatService).exec(45000, "gateway");
		assertThat(gatewayInfo.isResume()).isTrue();
	}

	@DisplayName("resumeフラグがtrueの場合はResumeを送信する")
	@Test
	void exec_sendsResumeWhenResumeIsTrue() {
		ReadyDetail readyDetail = mock(ReadyDetail.class);
		when(readyDetail.getSessionId()).thenReturn("session-abc");
		gatewayInfo.setReadyDetail(readyDetail);
		gatewayInfo.setSequence(42L);
		gatewayInfo.setResume(true);
		when(sendMessageService.getSession()).thenReturn(session);
		when(heartBeatServiceProvider.getHeartBeatService(session)).thenReturn(heartBeatService);

		Code10Dto dto = new Code10Dto(new Code10Detail(45000, null), null, null);
		service.exec(dto);

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(sendMessageService).sendMessage(captor.capture());
		assertThat(captor.getValue()).contains("\"op\":6");

		verify(heartBeatService).exec(45000, "gateway");
		assertThat(gatewayInfo.isResume()).isTrue();
	}

	@DisplayName("Code10Dto以外を受信した場合は何もしない")
	@Test
	void exec_doesNothingWhenDtoIsNotCode10Dto() {
		service.exec(mock(OpCodeReceiveBaseDto.class));

		verifyNoInteractions(sendMessageService, heartBeatServiceProvider);
	}
}
