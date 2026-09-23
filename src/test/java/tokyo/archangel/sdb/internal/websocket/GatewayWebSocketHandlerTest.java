package tokyo.archangel.sdb.internal.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.internal.api.DiscordApi;
import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.config.ApplicationProperties;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.internal.servicies.gateway.GatewayConnectionService;
import tokyo.archangel.sdb.internal.servicies.gateway.GatewayService;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageServiceProvider;

/**
 * 接続・再接続まわりの単体テスト。
 * {@link GatewayWebSocketHandler#afterConnectionClosed}
 * が呼ばれるたびに再接続まで1秒スリープするため、テスト全体の実行に数秒かかる。
 */
@ExtendWith(MockitoExtension.class)
class GatewayWebSocketHandlerTest {

	@Mock
	private GatewayService discordMainService;

	@Mock
	private DiscordApi api;

	@Mock
	private GatewayConnectionService gatewayConnectionService;

	@Mock
	private SendMessageServiceProvider sendMessageServiceProvider;

	@Mock
	private ApplicationContext context;

	@Mock
	private WebSocketSession session;

	private GatewayInfo gatewayInfo;

	private ApplicationProperties properties;

	private GatewayWebSocketHandler handler;

	@BeforeEach
	void setUp() {
		gatewayInfo = new GatewayInfo();
		properties = new ApplicationProperties();
		properties.setWebsocketMessageSizeLimit(4096);

		handler = new GatewayWebSocketHandler(discordMainService, api, properties, gatewayInfo,
				gatewayConnectionService, sendMessageServiceProvider, context);
	}

	@DisplayName("初期設定と送信サービスの開始を行う")
	@Test
	void afterConnectionEstablished_initializesSessionAndStartsSendService() throws Exception {
		SendMessageService service = mock(SendMessageService.class);
		when(sendMessageServiceProvider.generateSendMessageService(session)).thenReturn(service);

		handler.afterConnectionEstablished(session);

		verify(session).setTextMessageSizeLimit(properties.getWebsocketMessageSizeLimit());
		verify(sendMessageServiceProvider).setChannelId(session, "gateway");
		verify(service).exec("gateway");
	}

	@DisplayName("受信内容をGatewayServiceへ委譲する")
	@Test
	void handleTextMessage_delegatesPayloadToGatewayService() throws Exception {
		SendMessageService service = mock(SendMessageService.class);
		when(sendMessageServiceProvider.getServiceByChannelId("gateway")).thenReturn(service);
		TextMessage message = new TextMessage("{\"op\":11}");

		handler.handleTextMessage(session, message);

		verify(discordMainService).receive("{\"op\":11}", service);
	}

	@DisplayName("正常終了時は失敗回数を加算せずresumeフラグの状態で再接続する")
	@Test
	void afterConnectionClosed_reconnectsWithoutIncrementingFailCountOnNormalClosure() throws Exception {
		ReadyDetail readyDetail = mock(ReadyDetail.class);
		when(readyDetail.getResumeGatewayUrl()).thenReturn("wss://resume.example.com");
		gatewayInfo.setReadyDetail(readyDetail);
		gatewayInfo.setResume(true);
		gatewayInfo.setConnectionFailCount(0);

		handler.afterConnectionClosed(session, new CloseStatus(1000, "bye"));

		verify(discordMainService).close(session);
		assertThat(gatewayInfo.getConnectionFailCount()).isZero();
		assertThat(gatewayInfo.isResume()).isTrue();
		verify(gatewayConnectionService).connect("wss://resume.example.com/?v=10&encoding=json");
		verify(api, never()).getGatewayUrl();
	}

	@DisplayName("退出時も失敗回数を加算せずresumeフラグに応じたURLで再接続する")
	@Test
	void afterConnectionClosed_reconnectsWithoutIncrementingFailCountOnGoingAway() throws Exception {
		gatewayInfo.setResume(false);
		gatewayInfo.setConnectionFailCount(0);
		when(api.getGatewayUrl()).thenReturn("wss://gateway.example.com");

		handler.afterConnectionClosed(session, new CloseStatus(1001, "going away"));

		assertThat(gatewayInfo.getConnectionFailCount()).isZero();
		assertThat(gatewayInfo.isResume()).isFalse();
		verify(gatewayConnectionService).connect("wss://gateway.example.com/?v=10&encoding=json");
	}

	@DisplayName("異常終了かつ再接続可能なコードは失敗回数を加算しresumeフラグを維持する")
	@Test
	void afterConnectionClosed_incrementsFailCountAndKeepsResumeOnReconnectableAbnormalCode() throws Exception {
		ReadyDetail readyDetail = mock(ReadyDetail.class);
		when(readyDetail.getResumeGatewayUrl()).thenReturn("wss://resume.example.com");
		gatewayInfo.setReadyDetail(readyDetail);
		gatewayInfo.setResume(true);
		gatewayInfo.setConnectionFailCount(0);

		// 4000: UNKNOWN_ERROR（再接続可能）
		handler.afterConnectionClosed(session, new CloseStatus(4000, "unknown error"));

		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(1);
		assertThat(gatewayInfo.isResume()).isTrue();
		verify(gatewayConnectionService).connect("wss://resume.example.com/?v=10&encoding=json");
	}

	@DisplayName("異常終了かつ再接続不可能なコードはresumeフラグを解除して新規接続する")
	@Test
	void afterConnectionClosed_clearsResumeAndReconnectsFreshOnNonReconnectableAbnormalCode() throws Exception {
		gatewayInfo.setResume(true);
		gatewayInfo.setConnectionFailCount(0);
		when(api.getGatewayUrl()).thenReturn("wss://gateway.example.com");

		// 4004: AUTHENTICATION_FAILED（再接続不可能）
		handler.afterConnectionClosed(session, new CloseStatus(4004, "authentication failed"));

		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(1);
		assertThat(gatewayInfo.isResume()).isFalse();
		verify(gatewayConnectionService).connect("wss://gateway.example.com/?v=10&encoding=json");
	}

	@DisplayName("未定義のクローズコードは再接続可能として扱われる")
	@Test
	void afterConnectionClosed_treatsUndefinedCloseCodeAsReconnectable() throws Exception {
		ReadyDetail readyDetail = mock(ReadyDetail.class);
		when(readyDetail.getResumeGatewayUrl()).thenReturn("wss://resume.example.com");
		gatewayInfo.setReadyDetail(readyDetail);
		gatewayInfo.setResume(true);
		gatewayInfo.setConnectionFailCount(0);

		// 1006はenumに未定義（今回のバグの実例: SSL異常切断など）
		handler.afterConnectionClosed(session, new CloseStatus(1006, "abnormal closure"));

		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(1);
		assertThat(gatewayInfo.isResume()).isTrue();
		verify(gatewayConnectionService).connect("wss://resume.example.com/?v=10&encoding=json");
	}

	@DisplayName("失敗回数が5回に達してもアプリケーションを終了せず再接続する")
	@Test
	void afterConnectionClosed_doesNotExitWhenFailCountReachesThreshold() throws Exception {
		ReadyDetail readyDetail = mock(ReadyDetail.class);
		when(readyDetail.getResumeGatewayUrl()).thenReturn("wss://resume.example.com");
		gatewayInfo.setReadyDetail(readyDetail);
		gatewayInfo.setResume(true);
		gatewayInfo.setConnectionFailCount(4);

		handler.afterConnectionClosed(session, new CloseStatus(4000, "unknown error"));

		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(5);
		verify(gatewayConnectionService).connect("wss://resume.example.com/?v=10&encoding=json");
	}

	@DisplayName("失敗回数が5回を超えるとアプリケーションを終了し再接続しない")
	@Test
	void afterConnectionClosed_exitsApplicationWhenFailCountExceedsThreshold() throws Exception {
		gatewayInfo.setConnectionFailCount(5);
		gatewayInfo.setResume(true);

		try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
			handler.afterConnectionClosed(session, new CloseStatus(4000, "unknown error"));

			springApplication.verify(() -> SpringApplication.exit(eq(context), any(ExitCodeGenerator.class)));
		}

		// 閾値超過時は早期returnするため、失敗回数は更新されず再接続も行われない
		assertThat(gatewayInfo.getConnectionFailCount()).isEqualTo(5);
		verify(gatewayConnectionService, never()).connect(anyString());
		verify(api, never()).getGatewayUrl();
	}

	@DisplayName("シャットダウン中は再接続もアプリケーション終了も行わない")
	@Test
	void afterConnectionClosed_doesNothingWhileShuttingDown() throws Exception {
		handler.onShutdown();
		gatewayInfo.setConnectionFailCount(0);

		handler.afterConnectionClosed(session, new CloseStatus(4000, "unknown error"));

		verify(discordMainService).close(session);
		assertThat(gatewayInfo.getConnectionFailCount()).isZero();
		verify(gatewayConnectionService, never()).connect(anyString());
		verify(api, never()).getGatewayUrl();
	}
}
