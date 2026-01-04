package tokyo.archangel.sdb.discord.api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * discordのAPIを叩くサービス
 */
@Slf4j
@Service
public class DiscordApi {
	private final RestClient restClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String BASE_URL = "https://discord.com/api/v10/";

	public DiscordApi(RestClient restClient) {
		this.restClient = restClient;
	}

	public String getGatewayUrl() {
		log.debug("ゲートウェイURL取得開始");

		String response = restClient.get()
				.uri(BASE_URL + "gateway")
				.retrieve()
				.body(String.class);

		String url = objectMapper.readTree(response).get("url").asString();

		log.debug("ゲートウェイURL取得完了");
		log.trace("取得url: " + url);

		return url;
	}

}
