package tokyo.archangel.sdb.discord.launcher;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.api.DiscordApi;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayConnectionService;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayHeartBeatCheckService;

/**
 * ディスコードのメインサービスを発火させるクラス
 */
@Component
@Slf4j
public class DiscordServiceLauncher implements CommandLineRunner {
	private DiscordApi api;

	private GatewayConnectionService gatewayConnectionService;

	private GatewayHeartBeatCheckService gatewayHeartBeatCheckService;

	public DiscordServiceLauncher(GatewayConnectionService gatewayConnectionService, DiscordApi api,
			GatewayHeartBeatCheckService gatewayHeartBeatCheckService) {
		this.gatewayConnectionService = gatewayConnectionService;
		this.api = api;
		this.gatewayHeartBeatCheckService = gatewayHeartBeatCheckService;
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("discordメインサービス起動開始");

		// 接続URL取得
		String gatewayUrl = api.getGatewayUrl();
		gatewayUrl += "/?v=10&encoding=json";

		// websocket接続
		gatewayConnectionService.connect(gatewayUrl);

		// ハートビートチェックスレッド起動
		gatewayHeartBeatCheckService.exec();
	}
}
