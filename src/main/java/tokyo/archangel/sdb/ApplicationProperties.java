package tokyo.archangel.sdb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * アプリケーションの設定を保持するクラス
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "sdb")
@Component
public class ApplicationProperties {
	/**
	 * discordボットトークン
	 */
	private String botToken;
	
	/**
	 * websocketの送受信最大サイズ（単位バイト）
	 */
	private int websocketMessageSizeLimit;
}
