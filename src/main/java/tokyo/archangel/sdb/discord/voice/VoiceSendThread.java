package tokyo.archangel.sdb.discord.voice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;
import tokyo.archangel.sdb.discord.servicies.libdave.DaveService;
import tokyo.archangel.sdb.discord.servicies.libdave.DaveServiceProvider;
import tokyo.archangel.sdb.discord.servicies.libopus.OpusEncodeService;
import tokyo.archangel.sdb.discord.servicies.transportcrypter.TransportCryptService;
import tokyo.archangel.sdb.discord.servicies.transportcrypter.TransportCryptServiceProvider;
import tokyo.archangel.sdb.discord.udp.UdpConnection;
import tokyo.archangel.sdb.discord.udp.UdpConnectionProvider;

@Component
@Scope("prototype")
@Slf4j
public class VoiceSendThread {

	private DaveServiceProvider daveServiceProvider;

	private TransportCryptServiceProvider transportCryptServiceProvider;

	private OpusEncodeService opusEncoder;

	private UdpConnectionProvider udpConnectionProvider;

	private VoiceChannelInfo voiceInfo;

	private VoiceBinaryBuffer buffer;

	private short sequence = 0;

	private int timestamp = 0;

	private long nextTargetTime;

	private volatile ServiceThreadStatus status = ServiceThreadStatus.INITIALIZING;

	public VoiceSendThread(DaveServiceProvider daveServiceProvider,
			TransportCryptServiceProvider transportCryptServiceProvider,
			OpusEncodeService opusEncoder,
			UdpConnectionProvider udpConnectionProvider) {
		this.daveServiceProvider = daveServiceProvider;
		this.transportCryptServiceProvider = transportCryptServiceProvider;
		this.opusEncoder = opusEncoder;
		this.udpConnectionProvider = udpConnectionProvider;
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
	public void send() {
		status = ServiceThreadStatus.ACTIVE;
		UdpConnection udpConnection = udpConnectionProvider.getUdpConnection(voiceInfo.getChannelId());
		DaveService daveService = daveServiceProvider.getDaveService(voiceInfo.getWebsocketGuid());
		TransportCryptService cryptService = transportCryptServiceProvider.generateCryptService(
				voiceInfo.getWebsocketGuid(),
				voiceInfo.getInfo().getSecretKey());

		nextTargetTime = System.nanoTime();
		while (status == ServiceThreadStatus.ACTIVE) {
			byte[] data = buffer.get();
			send(data, daveService, cryptService, udpConnection);
		}
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		status = ServiceThreadStatus.TERMINATING;
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

	private void send(byte[] data, DaveService daveService, TransportCryptService cryptService,
			UdpConnection udpConnection) {
		if (data == null || data.length == 0 || udpConnection == null) {
			sleep();
			return;
		}

		// OPUSでエンコード
		byte[] opusData = opusEncoder.encode(byteArrayToShortArray(data));

		// E2EE暗号化
		byte[] e2eeEncryptData = daveService.encryptOpus(opusData);

		// トランスポート層暗号化
		byte[] sendableData = cryptService.encryptPacket(addRtpHeader(), e2eeEncryptData);

		try {
			sleep();
			udpConnection.send(sendableData);
		} catch (IOException e) {
			log.error("音声送信に失敗しました。", e);
		}
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
