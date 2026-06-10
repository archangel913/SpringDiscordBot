package tokyo.archangel.sdb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import tokyo.archangel.sdb.ApplicationProperties;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcode10Service;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcode11Service;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcode1Service;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcode7Service;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcode9Service;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch.ReadyEventService;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch.VoiceChannelStartTimeUpdateService;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch.VoiceChannelStatusUpdateService;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch.VoiceServerUpdateEventService;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch.VoiceStateUpdateService;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceConnectionService;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class DiscordBotGatewayConfiguration {
	@Bean
	@ConditionalOnMissingBean
	ObjectMapper discordBotGatewayConfiguration() {
		return new ObjectMapper();
	}

	/**
	 * opcode0,READYのサービスクラス
	 */
	@Bean
	ReadyEventService readyEventService(GatewayInfo gatewayInfo) {
		return new ReadyEventService(gatewayInfo);
	}

	/**
	 * opcode0,VOICE_CHANNEL_START_TIME_UPDATEのサービスクラス
	 */
	@Bean
	VoiceChannelStartTimeUpdateService voiceChannelStartTimeUpdateService(VoiceChannels channels) {
		return new VoiceChannelStartTimeUpdateService(channels);
	}

	/**
	 * opcode0,VOICE_CHANNEL_STATUS_UPDATEのサービスクラス
	 */
	@Bean
	VoiceChannelStatusUpdateService voiceChannelStatusUpdateService(VoiceChannels channels) {
		return new VoiceChannelStatusUpdateService(channels);
	}

	/**
	 * opcode0,VOICE_SERVER_UPDATEのサービスクラス
	 */
	@Bean
	VoiceServerUpdateEventService voiceServerUpdateEventService(VoiceConnectionService voiceConnectionService) {
		return new VoiceServerUpdateEventService(voiceConnectionService);
	}

	/**
	 * opcode0,VOICE_STATE_UPDATEのサービスクラス
	 */
	@Bean
	VoiceStateUpdateService voiceStateUpdateService(VoiceChannels channels) {
		return new VoiceStateUpdateService(channels);
	}

	/**
	 * opcode1のサービスクラス
	 */
	@Bean
	GatewayOpcode1Service gatewayOpcode1Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		return new GatewayOpcode1Service(heartBeatServiceProvider);
	}

	/**
	 * opcode7のサービスクラス
	 */
	@Bean
	GatewayOpcode7Service gatewayOpcode7Service(HeartBeatServiceProvider heartBeatServiceProvider,
			GatewayInfo gatewayInfo) {
		return new GatewayOpcode7Service(heartBeatServiceProvider, gatewayInfo);
	}

	/**
	 * opcode9のサービスクラス
	 */
	@Bean
	GatewayOpcode9Service gatewayOpcode9Service(HeartBeatServiceProvider heartBeatServiceProvider,
			GatewayInfo gatewayInfo) {
		return new GatewayOpcode9Service(heartBeatServiceProvider, gatewayInfo);
	}

	/**
	 * opcode10のサービスクラス
	 */
	@Bean
	GatewayOpcode10Service gatewayOpcode10Service(HeartBeatServiceProvider heartBeatServiceProvider,
			GatewayInfo gatewayInfo, Environment environment, ApplicationProperties properties) {
		return new GatewayOpcode10Service(heartBeatServiceProvider, gatewayInfo, environment, properties);
	}

	/**
	 * opcode11のサービスクラス
	 */
	@Bean
	GatewayOpcode11Service gatewayOpcode11Service(HeartBeatServiceProvider heartBeatServiceProvider) {
		return new GatewayOpcode11Service(heartBeatServiceProvider);
	}
}
