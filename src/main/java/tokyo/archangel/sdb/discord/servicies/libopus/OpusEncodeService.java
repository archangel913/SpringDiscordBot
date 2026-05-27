package tokyo.archangel.sdb.discord.servicies.libopus;

import org.springframework.stereotype.Service;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

@Service
public class OpusEncodeService {
	private final Pointer encoder;
	private static final int OPUS_APPLICATION_AUDIO = 2049;

	public OpusEncodeService() {
		// 1. エンコーダーに必要なメモリサイズを取得
		int size = OpusEncorder.INSTANCE.opus_encoder_get_size(2);
		this.encoder = new Memory(size);

		// 2. 初期化 (48kHz, ステレオ, AUDIOモード)
		int error = OpusEncorder.INSTANCE.opus_encoder_init(encoder, 48000, 2, OPUS_APPLICATION_AUDIO);
		if (error < 0)
			throw new RuntimeException("Opus初期化失敗: " + error);
	}

	public byte[] encode(short[] pcmFrame) {
		// 出力バッファ（通常1275バイトあれば十分）
		byte[] outBuffer = new byte[1275];

		// 3. エンコード実行 (frame_sizeは1サンプルあたりの数: 20msなら960)
		int len = OpusEncorder.INSTANCE.opus_encode(encoder, pcmFrame, 960, outBuffer, outBuffer.length);

		if (len < 0)
			throw new RuntimeException("エンコード失敗");

		// 実際のデータ長だけ切り出す
		byte[] result = new byte[len];
		System.arraycopy(outBuffer, 0, result, 0, len);
		return result;
	}
}
