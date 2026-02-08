package tokyo.archangel.sdb;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import tokyo.archangel.sdb.discord.enumeration.Intent;

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
	 * ディスコードへメッセージを送信するレート制限
	 */
	private int websocketSendRateLimit;
	
	/**
	 * websocketの送受信最大サイズ（単位バイト）
	 */
	private int websocketMessageSizeLimit;
	
	/**
	 * ボットに許可するインテント
	 */
	private List<Intent> intents;
}
