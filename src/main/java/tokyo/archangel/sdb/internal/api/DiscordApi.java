package tokyo.archangel.sdb.internal.api;

import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * discordのAPIを叩くサービス
 */
@Slf4j
public class DiscordApi {
	private final RestClient restClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String BASE_URL = "https://discord.com/api/v10/";

	public DiscordApi(RestClient restClient) {
		this.restClient = restClient;
	}

	public String getGatewayUrl() {
		log.debug("ゲートウェイURL取得開始");

		String url;
		int errorCount = 0;
		while (true) {
			try {
				String response = restClient.get()
						.uri(BASE_URL + "gateway")
						.retrieve()
						.body(String.class);

				url = objectMapper.readTree(response).get("url").asString();
			} catch (Exception e) {
				log.warn("URLの取得に失敗しました。再取得します。");
				try {
					Thread.sleep((long)(Math.pow(2, errorCount)) * 1000);
				} catch (InterruptedException sleepEx) {
					// 中断されても何もしない
				}
				errorCount++;
				continue;
			}
			log.debug("ゲートウェイURL取得完了");
			log.trace("取得url: " + url);
			break;
		}

		return url;
	}

}
