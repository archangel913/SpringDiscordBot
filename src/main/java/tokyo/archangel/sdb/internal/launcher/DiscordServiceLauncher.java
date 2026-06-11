package tokyo.archangel.sdb.internal.launcher;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.config.ApplicationProperties;
import tokyo.archangel.sdb.internal.api.DiscordApi;
import tokyo.archangel.sdb.internal.enumeration.Intent;
import tokyo.archangel.sdb.internal.servicies.gateway.GatewayConnectionService;

/**
 * ディスコードのメインサービスを発火させるクラス
 */
@Slf4j
public class DiscordServiceLauncher implements CommandLineRunner {
	private static final int MINIMUM_WEBSOCKET_MESSAGE_SIZE_LIMIT = 500;
	private DiscordApi api;

	private GatewayConnectionService gatewayConnectionService;

	private ApplicationProperties properties;

	private ApplicationContext context;

	public DiscordServiceLauncher(GatewayConnectionService gatewayConnectionService, DiscordApi api,
			ApplicationProperties properties, ApplicationContext context) {
		this.gatewayConnectionService = gatewayConnectionService;
		this.api = api;
		this.properties = properties;
		this.context = context;
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			validate();
		} catch (Exception e) {
			log.error("起動に失敗しました。", e);
			SpringApplication.exit(context, () -> 1);
		}

		log.info("discordメインサービス起動開始");

		// 接続URL取得
		String gatewayUrl = api.getGatewayUrl();
		gatewayUrl += "/?v=10&encoding=json";

		// websocket接続
		gatewayConnectionService.connect(gatewayUrl);
	}

	private void validate() throws IllegalStateException {
		// TODO その他マジックナンバーを削除する
		String botToken = properties.getBotToken();
		if (botToken == null || botToken.isBlank()) {
			throw new IllegalStateException("ボットトークンは必ず必要です。設定してください");
		}

		List<Intent> intents = properties.getIntents();
		if (intents == null || intents.size() == 0) {
			throw new IllegalStateException("インテントは必ず必要です。設定してください");
		}

		int websocketMessageSizeLimit = properties.getWebsocketMessageSizeLimit();
		if (websocketMessageSizeLimit < 1048576) {
			log.warn("websocketの送受信バッファが少なすぎる可能性があります。");
		}

		int websocketSendRateLimit = properties.getWebsocketSendRateLimit();
		if (websocketSendRateLimit < MINIMUM_WEBSOCKET_MESSAGE_SIZE_LIMIT) {
			websocketSendRateLimit = MINIMUM_WEBSOCKET_MESSAGE_SIZE_LIMIT;
			log.warn("discordの最小送信レートを下回っています。メッセージ送信間隔を500msに設定します。");
		}

	}
}
