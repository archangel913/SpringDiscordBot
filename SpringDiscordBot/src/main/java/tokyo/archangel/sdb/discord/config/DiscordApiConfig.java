package tokyo.archangel.sdb.discord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import tokyo.archangel.sdb.discord.api.DiscordApi;

/**
 * ディスコードへ投げるAPIを管理するためのコンフィグクラス<br>
 * シングルトンとする
 */
@Configuration
public class DiscordApiConfig {

    @Bean
    DiscordApi discordApi() {
		return new DiscordApi(RestClient.create());
	}
}
