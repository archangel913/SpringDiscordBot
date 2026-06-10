package tokyo.archangel.sdb.discord.servicies.voice;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code5.Code5Detail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code5.Code5Dto;
import tokyo.archangel.sdb.discord.enumeration.ConnectingState;
import tokyo.archangel.sdb.discord.enumeration.Speaking;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.libdave.E2eeCryptService;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcodeServiceFactory;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class VoiceService {

	private VoiceOpcodeServiceFactory opcodeServiceFactory;

	private VoiceConnectionService connectionService;

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private VoiceResourceProvider voiceSessionProvider;

	private VoiceChannels channels;

	private SendMessageServiceProvider sendMessageServiceProvider;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public VoiceService(VoiceOpcodeServiceFactory opcodeServiceFactory,
			VoiceConnectionService connectionService,
			HeartBeatServiceProvider heartBeatServiceProvider,
			VoiceResourceProvider voiceSessionProvider, VoiceChannels channels,
			SendMessageServiceProvider sendMessageServiceProvider) {
		this.opcodeServiceFactory = opcodeServiceFactory;
		this.connectionService = connectionService;
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.voiceSessionProvider = voiceSessionProvider;
		this.channels = channels;
		this.sendMessageServiceProvider = sendMessageServiceProvider;
	}

	public void receive(String json, SendMessageService service) {
		log.trace("受信メッセージ: " + json);
		try {
			OpCodeReceiveBaseDto baseDto = objectMapper.readValue(json, OpCodeReceiveBaseDto.class);
			receive(baseDto, service);
		} catch (JacksonException e) {
			log.warn("jsonのパースに失敗しました。何も行いません。");
		} catch (Exception e) {
			log.warn("例外が発生しました。", e);
		}
	}

	public void recieve(ByteBuffer data, SendMessageService sendMessageService) {
		log.trace("バイナリを受信しました。サイズ: " + data.array().length);

		E2eeCryptService daveService = voiceSessionProvider
				.getE2eeCryptService(sendMessageService.getSession().getId());
		if (daveService == null) {
			log.error("E2EEサービスの取得に失敗しました。処理を実行しません。");
			return;
		}
		int opcode = data.get();
		byte[] payload = Arrays.copyOfRange(data.array(), 1, data.array().length);
		try {
			if (opcode == 25) {
				log.debug("外部送信者を処理します");
				daveService.processExternalSender(payload);

				log.debug("key packageを送信します");
				byte[] keyPackage = daveService.getMarshalledKeyPackage();
				byte[] returnPayload = ByteBuffer.allocate(1 + keyPackage.length)
						.put((byte) 26)
						.put(keyPackage)
						.array();

				// 生成したキーパッケージを送り返す
				sendMessageService.sendMessage(returnPayload);
			} else if (opcode == 27) {
				log.debug("提案を処理します");
				byte[] result = daveService.processProposals(payload);
				byte[] returnPayload = ByteBuffer.allocate(1 + result.length)
						.put((byte) 28)
						.put(result)
						.array();

				// 提案処理で生成されたウェルカムバイナリを送り返す
				sendMessageService.sendMessage(returnPayload);
			} else if (opcode == 29) {
				// オフセット0はopcode, 1と2は何????
				payload = Arrays.copyOfRange(data.array(), 3, data.array().length);
				daveService.processCommit(payload);
				daveService.updateEncryptorRachet();
			} else if (opcode == 30) {
				log.debug("welcomeを処理します");

				// オフセット0はopcode, 1と2は何????
				payload = Arrays.copyOfRange(data.array(), 3, data.array().length);
				daveService.processWelcome(payload);
				daveService.updateEncryptorRachet();

				// 音声が再生可能となったことをサーバーに通知

				// これから音声を送信することをサーバーに通知
				VoiceChannelInfo voiceInfo = channels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
				sendMessageService.sendMessage(getSpeakingJson(voiceInfo));
				voiceInfo.getReadyFuture().complete(null);
			}
		} catch (Exception e) {
			// TODO 暫定的にすべて例外捕捉しているが、
			// 今後は個別に対応する必要あり。
			log.error("E2EEサービス実行時にエラーが発生しました。", e);
		}
	}

	public void reconnect(SendMessageService sendMessageService) {
		// 音声ゲートウェイは常に接続されている必要がある
		// ステータスコードにかかわらず、再接続を実施する
		VoiceChannelInfo voiceInfo = channels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
		int failCount = voiceInfo.getConnectionFailCount() + 1;
		if (failCount > 5) {
			// 既定回数以上失敗したらアプリケーションを落とす
			log.error("接続に5回連続で失敗しました。音声接続を切断します。");

			// 音声接続切断処理
			channels.removeInfoByChannelId(voiceInfo.getChannelId());
			return;
		}

		voiceInfo.setConnectionFailCount(failCount);

		if (voiceInfo.getConnectingState() == ConnectingState.RESUMING) {
			// opcode7で再開を行う場合
			connectionService.reconnect(voiceInfo);

			// サービスのGUIDを付け替える
			voiceSessionProvider.moveDaveService(voiceInfo.getOldWebsocketGuid(), voiceInfo.getWebsocketGuid());
		} else {
			// 認証からやり直す場合

			// 音声を再生不可にする
			voiceInfo.setReadyFuture(new CompletableFuture<>());

			SendMessageService messageService = sendMessageServiceProvider.getServiceByChannelId("gateway");
			Code4Dto dto = new Code4Dto(
					new Code4Detail(voiceInfo.getGuildId(), voiceInfo.getChannelId(), voiceInfo.isMute(),
							voiceInfo.isDeaf()));
			String json = objectMapper.writeValueAsString(dto);
			messageService.sendMessage(json);
		}
	}

	/**
	 * ステータスを切断中にする
	 * @param session
	 */
	public void setDisconnectingStatus(WebSocketSession session) {
		VoiceChannelInfo voiceInfo = channels.getInfoByWebsocketGuid(session.getId());
		voiceInfo.setConnectingState(ConnectingState.DISCONNECTING);
	}

	/**
	 * もろもろのスレッドを終了させる
	 * @param sendMessageService
	 * @return 再接続しないのであればtrue
	 */
	public boolean close(WebSocketSession session) {
		VoiceChannelInfo voiceInfo = channels.getInfoByWebsocketGuid(session.getId());
		String channelId = voiceInfo.getChannelId();

		heartBeatServiceProvider.removeService(session);

		// 再開の時
		if (voiceInfo.getConnectingState() == ConnectingState.CONNECTED) {
			log.debug("再開します。");
			voiceInfo.setConnectingState(ConnectingState.RESUMING);
			return false;
		}

		// UDP・暗号化まわりの削除
		voiceSessionProvider.removeServicies(session.getId());

		// 切断の時
		if (voiceInfo.getConnectingState() == ConnectingState.DISCONNECTING) {
			voiceInfo.setConnectingState(ConnectingState.DISCONNECTED);
			channels.removeInfoByChannelId(channelId);
			return true;
		}

		// ここに来るときは再開も失敗して認証からやり直す必要があるとき
		voiceInfo.setConnectingState(ConnectingState.RECONNECTING);
		log.debug("再接続します。");
		return false;
	}

	private void receive(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		VoiceOpcodeServiceInterface service = opcodeServiceFactory.create(baseDto, sendMessageService);
		if (service == null) {
			log.warn("サービスの取得に失敗しました。");
			return;
		}
		service.exec(baseDto);
	}

	private String getSpeakingJson(VoiceChannelInfo voiceInfo) {
		List<Speaking> speakingList = new ArrayList<Speaking>();
		speakingList.add(Speaking.MICROPHONE);
		int speakingFlag = Speaking.buildFlag(speakingList);

		Code5Dto speakingDto = new Code5Dto(new Code5Detail(speakingFlag, 0, voiceInfo.getSsrc()));
		return objectMapper.writeValueAsString(speakingDto);
	}
}
