package tokyo.archangel.sdb.internal.servicies.gateway;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import tokyo.archangel.sdb.internal.websocket.GatewayWebSocketHandler;

/**
 * {@link GatewayConnectionService}の単体テスト。<br>
 * {@link WebSocketClientProvider}をモック化することで、実際のネットワーク接続を行わずに検証する。
 */
@ExtendWith(MockitoExtension.class)
class GatewayConnectionServiceTest {

	@Mock
	private GatewayWebSocketHandler discordWebSocketHandler;

	@Mock
	private WebSocketClientProvider webSocketClientProvider;

	@Mock
	private WebSocketClient webSocketClient;

	private GatewayConnectionService service;

	@BeforeEach
	void setUp() {
		when(webSocketClientProvider.getWebSocketClient()).thenReturn(webSocketClient);
		service = new GatewayConnectionService(discordWebSocketHandler, webSocketClientProvider);
	}

	@DisplayName("プロバイダーから取得したクライアントでハンドシェイクを実行する")
	@Test
	void connect_executesHandshakeUsingClientFromProvider() {
		WebSocketSession session = mock(WebSocketSession.class);
		when(webSocketClient.execute(discordWebSocketHandler, "wss://gateway.example.com"))
				.thenReturn(CompletableFuture.completedFuture(session));

		service.connect("wss://gateway.example.com");

		verify(webSocketClientProvider).getWebSocketClient();
		verify(webSocketClient).execute(discordWebSocketHandler, "wss://gateway.example.com");
	}

	@DisplayName("接続失敗時も例外を伝播させない")
	@Test
	void connect_doesNotPropagateExceptionOnConnectionFailure() {
		CompletableFuture<WebSocketSession> failedFuture = new CompletableFuture<>();
		failedFuture.completeExceptionally(new IOException("connection refused"));
		when(webSocketClient.execute(discordWebSocketHandler, "wss://gateway.example.com"))
				.thenReturn(failedFuture);

		assertThatCode(() -> service.connect("wss://gateway.example.com")).doesNotThrowAnyException();
	}

	@DisplayName("接続の度にプロバイダーから新しいクライアントを取得する")
	@Test
	void connect_requestsNewClientFromProviderOnEachCall() {
		when(webSocketClient.execute(eq(discordWebSocketHandler), anyString()))
				.thenReturn(CompletableFuture.completedFuture(mock(WebSocketSession.class)));

		service.connect("wss://first.example.com");
		service.connect("wss://second.example.com");

		verify(webSocketClientProvider, times(2)).getWebSocketClient();
	}
}
