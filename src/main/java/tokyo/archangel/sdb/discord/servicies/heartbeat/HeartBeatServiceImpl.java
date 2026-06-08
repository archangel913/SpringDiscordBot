package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.gateway.GatewayInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code1.Code1SendDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code3.Code3ReceiveDto;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tools.jackson.databind.ObjectMapper;

/**
 * ハートビート送信を管理するクラス
 */
@Service
@Scope("prototype")
@Slf4j
public class HeartBeatServiceImpl implements HeartBeatService {
	private static int threadNumber = 0;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Random random = new Random();

	private final AtomicLong lastAckTime = new AtomicLong(0);

	private GatewayInfo gatewayInfo;

	private SendMessageService sendMessageService;

	// 音声gateway用
	private VoiceChannelInfo voiceChannelInfo;

	private volatile ServiceThreadStatus status = ServiceThreadStatus.INITIALIZING;

	private Thread currentThread;

	public HeartBeatServiceImpl(GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	@Override
	public void setVoiceChannelInfo(VoiceChannelInfo voiceChannelInfo) {
		this.voiceChannelInfo = voiceChannelInfo;
	}

	@Override
	public void setSendMessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

	@Async
	@Override
	public CompletableFuture<Void> exec(int interval, String channelId) {
		if (status == ServiceThreadStatus.ACTIVE) {
			log.debug("ハートビートスレッドがすでに起動しています");
			return CompletableFuture.completedFuture(null);
		}

		if (channelId == null) {
			// スレッド名表示用なのでエラー落ちさせるほどでは無い
			channelId = "";
		}

		status = ServiceThreadStatus.ACTIVE;
		currentThread = Thread.currentThread();
		currentThread.setName("sendHeartBeat-" + threadNumber + "-" + channelId);
		threadNumber++;
		lastAckTime.set(System.currentTimeMillis());
		log.debug("ハートビートスレッドを起動します。");

		try {
			Thread.sleep((long) (interval * random.nextDouble()));
			while (status == ServiceThreadStatus.ACTIVE) {
				long now = System.currentTimeMillis();
				long allowableDelay = (long) (interval * 1.25);
				if (now - lastAckTime.get() > allowableDelay) {
					log.warn("ハートビートの返信が確認できませんでした。");
					break;
				}

				log.debug("バックグラウンドでハートビートを送信します");
				String json = generateJson();
				if (json != null) {
					sendHeartBeat(json);
				}
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			// finallyで問題なく終了処理を行うために握りつぶす
		} catch (Exception e) {
			log.error("ハートビートスレッドが異常終了しました。", e);
		} finally {
			try {
				shutdown();
			} catch (Exception e) {
				log.warn("シャットダウンに失敗しました。メモリリークの可能性があります。", e);
			}
		}
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void receiveAck() {
		lastAckTime.set(System.currentTimeMillis());
	}

	@Override
	public void sendHeartBeat(String json) {
		sendMessageService.sendMessage(json);
	}

	@Override
	public void close() throws IllegalStateException {
		if (currentThread == null) {
			throw new IllegalStateException("停止対象のスレッドが不明です。");
		}
		// 既に完全に終了している場合は重ねて処理しない
		if (status == ServiceThreadStatus.TERMINATED) {
			return;
		}

		// 終了処理中でなければ、ステータスを TERMINATING に移行
		if (status != ServiceThreadStatus.TERMINATING) {
			status = ServiceThreadStatus.TERMINATING;
		}

		log.debug("ハートビートスレッドを終了します。");
		currentThread.interrupt();
	}

	private void shutdown() throws IllegalStateException {
		if (sendMessageService == null) {
			throw new IllegalStateException("メッセージ送信メソッドがnullです。");
		}
		sendMessageService.close();
		log.debug("ハートビートスレッドが終了しました。");
		// すべてのクリーンアップが完了したため、最終ステータスに固定
		status = ServiceThreadStatus.TERMINATED;
	}

	private String generateJson() {
		if (voiceChannelInfo == null) {
			// ゲートウェイのハートビートの場合
			return objectMapper.writeValueAsString(new Code1SendDto(gatewayInfo.getSequence()));
		} else {
			// ボイスのハートビートの場合
			// 使い捨ての値を取得
			long nonce = random.nextLong();
			return objectMapper.writeValueAsString(new Code3ReceiveDto(nonce));
		}
	}
}
