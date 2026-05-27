package tokyo.archangel.sdb.discord.servicies.voice;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code5.Code5Detail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code5.Code5Dto;
import tokyo.archangel.sdb.discord.enumeration.Speaking;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.libdave.DaveService;
import tokyo.archangel.sdb.discord.servicies.libdave.DaveServiceProvider;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcodeServiceFactory;
import tokyo.archangel.sdb.discord.servicies.opcode.voice.VoiceOpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * 
 */
@Service
@Slf4j
public class VoiceService {

	private VoiceOpcodeServiceFactory opcodeServiceFactory;

	private VoiceConnectionService connectionService;

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private DaveServiceProvider daveServiceProvider;

	private VoiceChannels channels;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public VoiceService(VoiceOpcodeServiceFactory opcodeServiceFactory,
			@Lazy VoiceConnectionService connectionService,
			HeartBeatServiceProvider heartBeatServiceProvider,
			DaveServiceProvider daveServiceProvider, VoiceChannels channels) {
		this.opcodeServiceFactory = opcodeServiceFactory;
		this.connectionService = connectionService;
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.daveServiceProvider = daveServiceProvider;
		this.channels = channels;
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

		DaveService daveService = daveServiceProvider.getDaveService(sendMessageService.getSession().getId());
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
			channels.removeInfoBySessionId(voiceInfo.getChannelId());
			return;
		}

		voiceInfo.setConnectionFailCount(failCount);

		connectionService.reconnect(voiceInfo);
		log.info("再接続が完了しました");
	}

	/**
	 * もろもろのスレッドを終了させる
	 * @param sendMessageService
	 */
	public void dispose(SendMessageService sendMessageService) {
		// ハートビート、ハートビートチェックスレッド、メッセージスレッドすべて止めることができる
		HeartBeatService heartBeatService = heartBeatServiceProvider
				.getHeartBeatService(sendMessageService.getSession());
		heartBeatService.dispose();
		heartBeatServiceProvider.removeService(sendMessageService.getSession());

		// UDPのクリーンアップ必要？
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
