package tokyo.archangel.sdb.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import tokyo.archangel.sdb.internal.enumeration.Intent;

/**
 * アプリケーションの設定を保持するクラス
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sdb")
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
