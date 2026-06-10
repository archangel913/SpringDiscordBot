package tokyo.archangel.sdb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.servicies.udp.selectprotcol.SelectProtcol;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class DiscordBotUdpConfiguration {
	@Bean
	@ConditionalOnMissingBean
	ObjectMapper discordBotUdpConfiguration() {
		return new ObjectMapper();
	}
	
	/**
	 * ハートビートのプロバイダークラス
	 */
	@Bean
	SelectProtcol selectProtcol(VoiceChannels channels, SendMessageServiceProvider messageProvider) {
		return new SelectProtcol(channels, messageProvider);
	}
}
