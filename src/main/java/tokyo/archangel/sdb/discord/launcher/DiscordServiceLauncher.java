package tokyo.archangel.sdb.discord.launcher;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.api.DiscordApi;
import tokyo.archangel.sdb.discord.component.GatewayInfo;
import tokyo.archangel.sdb.discord.enumeration.ReconnectMode;
import tokyo.archangel.sdb.discord.servicies.gateway.GatewayConnectionService;

/**
 * ディスコードのメインサービスを発火させるクラス
 */
@Component
@Slf4j
public class DiscordServiceLauncher implements CommandLineRunner {
	private DiscordApi api;

	private GatewayConnectionService gatewayConnectionService;
	
	private GatewayInfo gatewayInfo;

	public DiscordServiceLauncher(GatewayConnectionService gatewayConnectionService, DiscordApi api, GatewayInfo gatewayInfo) {
		this.gatewayConnectionService = gatewayConnectionService;
		this.api = api;
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("discordメインサービス起動開始");

		// 接続URL取得
		String gatewayUrl = api.getGatewayUrl();
		gatewayUrl += "/?v=10&encoding=json";
		
		// 再接続ではない
		gatewayInfo.setReconnectMode(ReconnectMode.NONE);

		// websocket接続
		gatewayConnectionService.connect(gatewayUrl);
	}
}
