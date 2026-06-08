package tokyo.archangel.sdb.discord.servicies.voice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.enumeration.ConnectingState;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;
import tokyo.archangel.sdb.discord.servicies.libdave.E2eeCryptService;
import tokyo.archangel.sdb.discord.servicies.libopus.OpusEncodeService;
import tokyo.archangel.sdb.discord.servicies.transportcrypter.TransportCryptService;
import tokyo.archangel.sdb.discord.udp.UdpConnection;
import tokyo.archangel.sdb.discord.voice.VoiceBinaryBuffer;

@Component
@Scope("prototype")
@Slf4j
public class VoiceSendServiceImpl implements VoiceSendService {
	private static int threadNumber = 0;

	private VoiceSessionProvider voiceSessionProvider;

	private OpusEncodeService opusEncoder;

	private VoiceChannelInfo voiceInfo;

	private VoiceBinaryBuffer buffer;

	private short sequence = 0;

	private int timestamp = 0;

	private long nextTargetTime;

	private volatile CompletableFuture<Void> pause = CompletableFuture.completedFuture(null);

	private volatile CompletableFuture<Void> sending = CompletableFuture.completedFuture(null);

	private volatile ServiceThreadStatus status = ServiceThreadStatus.INITIALIZING;

	public VoiceSendServiceImpl(VoiceSessionProvider voiceSessionProvider,
			OpusEncodeService opusEncoder) {
		this.voiceSessionProvider = voiceSessionProvider;
		this.opusEncoder = opusEncoder;
	}

	public void init(VoiceBinaryBuffer buffer, VoiceChannelInfo voiceInfo) {
		this.buffer = buffer;
		this.voiceInfo = voiceInfo;
		status = ServiceThreadStatus.INITIALIZED;
	}

	/**
	 * バッファから音声バイナリを取り出し、エンコード・暗号化して送信する
	 */
	@Async
	public CompletableFuture<Void> send() {
		if (status == ServiceThreadStatus.ACTIVE) {
			log.debug("音声送信スレッドがすでに起動しています");
			return CompletableFuture.completedFuture(null);
		}

		Thread currentThread = Thread.currentThread();
		currentThread.setName("VoiceSendService-" + threadNumber + "-" + voiceInfo.getChannelId());
		threadNumber++;
		status = ServiceThreadStatus.ACTIVE;
		sending = new CompletableFuture<>();

		try {
			UdpConnection udpConnection = voiceSessionProvider.getUdpConnection(voiceInfo.getWebsocketGuid());
			E2eeCryptService daveService = voiceSessionProvider.getE2eeCryptService(voiceInfo.getWebsocketGuid());
			TransportCryptService cryptService = voiceSessionProvider.getTransportCryptService(
					voiceInfo.getWebsocketGuid(),
					voiceInfo.getInfo().getSecretKey());

			nextTargetTime = System.nanoTime();
			while (status == ServiceThreadStatus.ACTIVE) {
				// 再認証による再接続があった際、UDP・暗号器がすべて書き変わる
				// 送信中にこれらが下記変わった場合、再取得する必要がある
				if (!voiceInfo.getReadyFuture().isDone()) {
					voiceInfo.getReadyFuture().join();
					udpConnection = voiceSessionProvider.getUdpConnection(voiceInfo.getChannelId());
					daveService = voiceSessionProvider.getE2eeCryptService(voiceInfo.getWebsocketGuid());
					cryptService = voiceSessionProvider.getTransportCryptService(
							voiceInfo.getWebsocketGuid(),
							voiceInfo.getInfo().getSecretKey());
					nextTargetTime = System.nanoTime();
					voiceInfo.setConnectingState(ConnectingState.CONNECTED);
				}

				if (!pause.isDone()) {
					sleep();
					continue;
				}
				byte[] data = buffer.get();
				send(data, daveService, cryptService, udpConnection);
			}
		} catch (Exception e) {
			log.error("音声再生中に致命的なエラーが発生しました", e);
		}
		status = ServiceThreadStatus.TERMINATED;
		sending.complete(null);
		log.debug("音声送信サービスを終了しました");
		return CompletableFuture.completedFuture(null);
	}

	public void pause() {
		if (!pause.isDone()) {
			return;
		}
		// 新しい未完了の Future を作成することで、送信スレッドをブロック状態にする
		pause = new CompletableFuture<>();
	}

	public void resume() {
		if (pause.isDone()) {
			return;
		}

		pause.complete(null);
		nextTargetTime = System.nanoTime();
	}

	public void close() {
		resume();
		status = ServiceThreadStatus.TERMINATING;
		sending.join();
	}

	private byte[] addRtpHeader() {
		byte[] withHeader = new byte[12];
		ByteBuffer buffer = ByteBuffer.wrap(withHeader);
		buffer.order(ByteOrder.BIG_ENDIAN);

		buffer.put((byte) 0x80);
		buffer.put((byte) 0x78);
		buffer.putShort(sequence);
		buffer.putInt(timestamp);
		buffer.putInt(voiceInfo.getSsrc());

		timestamp += 960;
		sequence++;

		return buffer.array();
	}

	private void send(byte[] data, E2eeCryptService daveService, TransportCryptService cryptService,
			UdpConnection udpConnection) throws IOException {
		if (data == null || data.length == 0) {
			sleep();
			return;
		}

		// OPUSでエンコード
		byte[] opusData = opusEncoder.encode(byteArrayToShortArray(data));

		// E2EE暗号化
		byte[] e2eeEncryptData = daveService.encryptOpus(opusData);

		// トランスポート層暗号化
		byte[] sendableData = cryptService.encryptPacket(addRtpHeader(), e2eeEncryptData);

		sleep();
		udpConnection.send(sendableData);
	}

	/**
	 * byte配列をshort配列に変換する
	 * @param byteArray 元の生データ（サイズは必ず偶数である必要があります）
	 * @return 変換されたshort配列
	 */
	private static short[] byteArrayToShortArray(byte[] byteArray) {
		short[] shortArray = new short[byteArray.length / 2];

		ByteBuffer.wrap(byteArray)
				.order(ByteOrder.LITTLE_ENDIAN)
				.asShortBuffer()
				.get(shortArray);

		return shortArray;
	}

	/**
	 * 前回の音声送信から20ms待機する
	 * @param sendedTime
	 */
	private void sleep() {
		long nextSendTime = nextTargetTime + 20_000_000;
		long remaining = nextSendTime - System.nanoTime();

		// もし目標時刻が現在時刻より100ms以上も過去になっていた場合、
		// 追いつくためのバースト（連投）を防ぐため、基準点を「今」に強制リセットする
		if (remaining < -100_000_000) {
			nextTargetTime = System.nanoTime();
			return;
		}

		while (remaining > 0) {
			if (remaining > 1_000_000) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					return;
				}
			} else {
				Thread.onSpinWait(); // 1ms未満の微調整
			}
			remaining = nextSendTime - System.nanoTime();
		}
		nextTargetTime += 20_000_000;
	}
}
