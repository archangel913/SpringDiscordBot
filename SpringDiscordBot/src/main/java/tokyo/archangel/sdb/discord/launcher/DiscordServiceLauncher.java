package tokyo.archangel.sdb.discord.launcher;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.api.DiscordApi;
import tokyo.archangel.sdb.discord.websocket.handler.MainDiscordWebSocketHandler;

/**
 * ディスコードのメインサービスを発火させるクラス
 */
@Component
@Slf4j
public class DiscordServiceLauncher implements CommandLineRunner {

	@Autowired
	private DiscordApi api;

	@Autowired
	private MainDiscordWebSocketHandler discordWebSocketHandler;

	public DiscordServiceLauncher() {
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("discordメインサービス起動開始");

		// 接続URL取得
		String gatewayUrl = api.getGatewayUrl();
		gatewayUrl += "/?v=10&encoding=json";

		// websocket生成
		WebSocketClient client = new StandardWebSocketClient();

		log.debug("websocket接続開始");
		client.execute(discordWebSocketHandler, gatewayUrl)
				.whenComplete((session, ex) -> {
					if (Objects.isNull(ex)) {
						log.debug("websocket接続完了");
					} else {
						log.error("websocket接続失敗", ex);
					}
				});
	}
}
