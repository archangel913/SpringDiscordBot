package tokyo.archangel.sdb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode11Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode13Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode22Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode2Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode3Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode4Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode8Service;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcode9Service;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceResourceProvider;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class DiscordBotVoiceGatewayConfiguration {
	@Bean
	@ConditionalOnMissingBean
	ObjectMapper discordBotVoiceGatewayConfiguration() {
		return new ObjectMapper();
	}

	/**
	 * opcode2のサービスクラス
	 */
	@Bean
	VoiceOpcode2Service voiceOpcode2Service(VoiceResourceProvider voiceResourceProvider,
			SendMessageServiceProvider messageServiceProvider, VoiceChannels channels) {
		return new VoiceOpcode2Service(voiceResourceProvider, messageServiceProvider, channels);
	}

	/**
	 * opcode3のサービスクラス
	 */
	@Bean
	VoiceOpcode3Service voiceOpcode3Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		return new VoiceOpcode3Service(heartBeatServiceProvider);
	}

	/**
	 * opcode4のサービスクラス
	 */
	@Bean
	VoiceOpcode4Service voiceOpcode4Service(VoiceChannels channels, VoiceResourceProvider voiceResourceProvider) {
		return new VoiceOpcode4Service(channels, voiceResourceProvider);
	}

	/**
	 * opcode8のサービスクラス
	 */
	@Bean
	VoiceOpcode8Service voiceOpcode8Service(HeartBeatServiceProvider heartBeatServiceProvider,
			VoiceChannels voiceChannels, SendMessageServiceProvider messageServiceProvider) {
		return new VoiceOpcode8Service(heartBeatServiceProvider, voiceChannels, messageServiceProvider);
	}

	/**
	 * opcode9のサービスクラス
	 */
	@Bean
	VoiceOpcode9Service voiceOpcode9Service(VoiceChannels voiceChannels) {
		return new VoiceOpcode9Service(voiceChannels);
	}

	/**
	 * opcode11のサービスクラス
	 */
	@Bean
	VoiceOpcode11Service voiceOpcode11Service(VoiceChannels voiceChannels) {
		return new VoiceOpcode11Service(voiceChannels);
	}

	/**
	 * opcode13のサービスクラス
	 */
	@Bean
	VoiceOpcode13Service voiceOpcode13Service(VoiceChannels voiceChannels) {
		return new VoiceOpcode13Service(voiceChannels);
	}

	/**
	 * opcode22のサービスクラス
	 */
	@Bean
	VoiceOpcode22Service voiceOpcode22Service() {
		return new VoiceOpcode22Service();
	}

}
