package tokyo.archangel.sdb.internal.servicies.libopus;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface OpusEncorder extends Library {
	// dllファイルをロード（PATHが通っている場所かプロジェクトルートに配置）
	OpusEncorder INSTANCE = Native.load("opus", OpusEncorder.class);

	// C言語の opus_encoder_get_size に対応
	int opus_encoder_get_size(int channels);

	// エンコーダーの初期化
	int opus_encoder_init(Pointer st, int Fs, int channels, int application);

	// エンコード実行
	int opus_encode(Pointer st, short[] pcm, int frame_size, byte[] data, int max_data_bytes);
}
