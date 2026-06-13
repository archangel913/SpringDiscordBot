package tokyo.archangel.sdb.config;

import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.internal.api.DiscordApi;
import tokyo.archangel.sdb.internal.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannels;
import tokyo.archangel.sdb.internal.launcher.DiscordServiceLauncher;
import tokyo.archangel.sdb.internal.servicies.gateway.GatewayConnectionService;
import tokyo.archangel.sdb.internal.servicies.gateway.GatewayService;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceImpl;
import tokyo.archangel.sdb.internal.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.internal.servicies.libdave.E2eeCryptServiceImpl;
import tokyo.archangel.sdb.internal.servicies.libopus.OpusEncodeService;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcodeServiceFactory;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.internal.servicies.opcode.voice.VoiceOpcodeServiceFactory;
import tokyo.archangel.sdb.internal.servicies.opcode.voice.VoiceOpcodeServiceInterface;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageServiceImpl;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.internal.servicies.transportcrypter.TransportCryptServiceImpl;
import tokyo.archangel.sdb.internal.servicies.udp.UdpServiceFactory;
import tokyo.archangel.sdb.internal.servicies.udp.UdpServiceInterface;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceConnectionService;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceResourceProvider;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceSendServiceImpl;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceService;
import tokyo.archangel.sdb.internal.udp.UdpConnectionImpl;
import tokyo.archangel.sdb.internal.udp.UdpService;
import tokyo.archangel.sdb.internal.voice.VoiceBinaryBuffer;
import tokyo.archangel.sdb.internal.voice.VoiceSenderImpl;
import tokyo.archangel.sdb.internal.websocket.GatewayWebSocketHandler;
import tokyo.archangel.sdb.internal.websocket.VoiceWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableAsync
@EnableConfigurationProperties(ApplicationProperties.class)
@Import({
		DiscordBotGatewayConfiguration.class,
		DiscordBotVoiceGatewayConfiguration.class,
		DiscordBotUdpConfiguration.class
})
public class DiscordBotConfiguration {
	@Bean
	@ConditionalOnMissingBean
	ObjectMapper discordBotObjectMapper() {
		return new ObjectMapper();
	}

	/**
	 * discordBotを起動するためのクラス
	 */
	@Bean
	DiscordServiceLauncher discordServiceLauncher(GatewayConnectionService gatewayConnectionService, DiscordApi api,
			ApplicationProperties properties, ApplicationContext context) {
		return new DiscordServiceLauncher(gatewayConnectionService, api, properties, context);
	}

	/**
	 * ゲートウェイの情報を保持するクラス
	 */
	@Bean
	GatewayInfo gatewayInfo() {
		return new GatewayInfo();
	}

	/**
	 * 音声ゲートウェイの情報を保持するクラス
	 */
	@Bean
	@Scope("prototype")
	VoiceChannelInfo voiceChannelInfo() {
		return new VoiceChannelInfo();
	}

	/**
	 * 音声ゲートウェイの情報をすべて保持するクラス
	 */
	@Bean
	VoiceChannels voiceChannels(ObjectProvider<VoiceChannelInfo> infomation) {
		return new VoiceChannels(infomation);
	}

	/**
	 * discordのapiを叩くためのクラス
	 */
	@Bean
	DiscordApi discordApi() {
		return new DiscordApi(RestClient.create());
	}

	/**
	 * ハートビートのプロバイダークラス
	 */
	@Bean
	HeartBeatServiceProvider heartBeatServiceProvider(ObjectProvider<HeartBeatServiceImpl> serviceProvider) {
		return new HeartBeatServiceProvider(serviceProvider);
	}

	/**
	 * ハートビートの実装クラス
	 */
	@Bean
	@Scope("prototype")
	HeartBeatServiceImpl heartBeatService(GatewayInfo gatewayInfo) {
		return new HeartBeatServiceImpl(gatewayInfo);
	}

	/**
	 * メッセージ送信のプロバイダークラス
	 */
	@Bean
	SendMessageServiceProvider sendMessageServiceProvider(ApplicationProperties properties,
			ObjectProvider<SendMessageServiceImpl> serviceProvider) {
		return new SendMessageServiceProvider(properties, serviceProvider);
	}

	/**
	 * メッセージ送信の実装クラス
	 */
	@Bean
	@Scope("prototype")
	SendMessageServiceImpl sendMessageService(ApplicationProperties properties, WebSocketSession session) {
		return new SendMessageServiceImpl(properties, session);
	}

	/**
	 * ゲートウェイ接続クラス
	 */
	@Bean
	GatewayConnectionService gatewayConnectionService(GatewayWebSocketHandler discordWebSocketHandler) {
		return new GatewayConnectionService(discordWebSocketHandler);
	}

	/**
	 * ゲートウェイサービスクラス
	 */
	@Bean
	GatewayService gatewayService(GatewayOpcodeServiceFactory opcodeServiceFactory, GatewayInfo gatewayInfo) {
		return new GatewayService(opcodeServiceFactory, gatewayInfo);
	}

	/**
	 * ゲートウェイサービスのファクトリクラス
	 */
	@Bean
	GatewayOpcodeServiceFactory gatewayOpcodeServiceFactory(
			Map<String, GatewayOpcodeServiceInterface> gatewayOpCodeServices) {
		return new GatewayOpcodeServiceFactory(gatewayOpCodeServices);
	}

	/**
	 * 音声ゲートウェイサービスのファクトリクラス
	 */
	@Bean
	VoiceOpcodeServiceFactory voiceOpcodeServiceFactory(Map<String, VoiceOpcodeServiceInterface> voiceOpCodeServices) {
		return new VoiceOpcodeServiceFactory(voiceOpCodeServices);
	}

	/**
	 * udpサービスのファクトリクラス
	 */
	@Bean
	UdpServiceFactory udpServiceFactory(Map<String, UdpServiceInterface> udpServiceInterface) {
		return new UdpServiceFactory(udpServiceInterface);
	}

	/**
	 * 音声送信サービスの実装クラス
	 */
	@Bean
	@Scope("prototype")
	VoiceSendServiceImpl voiceSendService(VoiceResourceProvider voiceSessionProvider,
			OpusEncodeService opusEncoder) {
		return new VoiceSendServiceImpl(voiceSessionProvider, opusEncoder);
	}

	/**
	 * ゲートウェイ用WebSocketハンドラークラス
	 */
	@Bean
	GatewayWebSocketHandler gatewayWebSocketHandler(GatewayService discordMainService, DiscordApi api,
			ApplicationProperties properties,
			GatewayInfo gatewayInfo, @Lazy GatewayConnectionService gatewayConnectionService,
			SendMessageServiceProvider sendMessageServiceProvider,
			ApplicationContext context) {
		return new GatewayWebSocketHandler(discordMainService, api, properties, gatewayInfo, gatewayConnectionService,
				sendMessageServiceProvider, context);
	}

	/**
	 * 音声ゲートウェイ用WebSocketハンドラークラス
	 */
	@Bean
	@Scope("prototype")
	VoiceWebSocketHandler voiceWebSocketHandler(VoiceService discordVoiceService, ApplicationProperties properties,
			SendMessageServiceProvider sendMessageServiceProvider) {
		return new VoiceWebSocketHandler(discordVoiceService, properties, sendMessageServiceProvider);
	}

	/**
	 * 音声ゲートウェイ接続クラス
	 */
	@Bean
	VoiceConnectionService voiceConnectionService(VoiceWebSocketHandler discordVoiceWebSocketHandler,
			SendMessageServiceProvider sendMessageServiceProvider, VoiceChannels channels) {
		return new VoiceConnectionService(discordVoiceWebSocketHandler, sendMessageServiceProvider, channels);
	}

	/**
	 * 音声サービス接続クラス
	 */
	@Bean
	VoiceService voiceService(VoiceOpcodeServiceFactory opcodeServiceFactory,
			@Lazy VoiceConnectionService connectionService, HeartBeatServiceProvider heartBeatServiceProvider,
			VoiceResourceProvider voiceSessionProvider, VoiceChannels channels,
			SendMessageServiceProvider sendMessageServiceProvider) {
		return new VoiceService(opcodeServiceFactory, connectionService, heartBeatServiceProvider, voiceSessionProvider,
				channels, sendMessageServiceProvider);
	}

	/**
	 * 音声リソースクラス
	 */
	@Bean
	VoiceResourceProvider voiceSessionProvider(ObjectProvider<UdpConnectionImpl> udpConnectionProvider,
			ObjectProvider<E2eeCryptServiceImpl> e2eeCryptServiceProvider,
			ObjectProvider<TransportCryptServiceImpl> transportCryptServiceProvider,
			ObjectProvider<VoiceSendServiceImpl> voiceSendServiceProvider) {
		return new VoiceResourceProvider(udpConnectionProvider, e2eeCryptServiceProvider, transportCryptServiceProvider,
				voiceSendServiceProvider);
	}

	/**
	 * 音声送信の実装クラス
	 */
	@Bean
	@Scope("prototype")
	VoiceSenderImpl voiceSender(SendMessageServiceProvider sendMessageServiceProvider,
			VoiceResourceProvider voiceSessionProvider, VoiceChannels voiceChannels, VoiceBinaryBuffer binaryBuffer,
			VoiceSendServiceImpl sendThread) {
		return new VoiceSenderImpl(sendMessageServiceProvider, voiceSessionProvider, voiceChannels, binaryBuffer,
				sendThread);
	}

	/**
	 * 音声送信のデータバッファクラス
	 */
	@Bean
	@Scope("prototype")
	VoiceBinaryBuffer voiceBinaryBuffer() {
		return new VoiceBinaryBuffer();
	}

	/**
	 * E2EE暗号化実装クラス
	 */
	@Bean
	@Scope("prototype")
	E2eeCryptServiceImpl e2eeCryptService() {
		return new E2eeCryptServiceImpl();
	}

	/**
	 * トランスポート暗号化実装クラス
	 */
	@Bean
	@Scope("prototype")
	TransportCryptServiceImpl transportCryptService() {
		return new TransportCryptServiceImpl();
	}

	/**
	 * opusエンコードクラス
	 */
	@Bean
	OpusEncodeService opusEncodeService() {
		return new OpusEncodeService();
	}

	/**
	 * udp接続サービス
	 */
	@Bean
	@Scope("prototype")
	UdpConnectionImpl udpConnection(UdpService service) {
		return new UdpConnectionImpl(service);
	}

	/**
	 * udpサービスクラス
	 */
	@Bean
	UdpService udpService(UdpServiceFactory factory) {
		return new UdpService(factory);
	}
}
