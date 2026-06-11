package tokyo.archangel.sdb.internal.voice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VoiceBinaryBuffer {

	private static final int OPUS_FRAME_LENGTH = 3840;

	private static final int BUFFER_SIZE = 65536;
	private static final int BUFFER_MASK = BUFFER_SIZE - 1;

	private final byte[] rawBuffer = new byte[BUFFER_SIZE];

	private int head = 0;
	private int tail = 0;
	private int size = 0;

	/**
	 * バッファにデータを追加する。
	 * バッファサイズを超える巨大なデータが渡されても、空き容量の分ずつ小分けに吸い込み、
	 * 満杯になったら安全に待機（バックプレッシャー）します。デッドロックは発生しません。
	 */
	public synchronized void add(byte[] data) throws InterruptedException {
		if (data == null || data.length == 0) {
			return;
		}

		int srcPos = 0;
		int remainBytes = data.length;

		while (remainBytes > 0) {
			// バッファが完全に満杯なら、空きができるまで眠る
			while (size == BUFFER_SIZE) {
				wait();
			}

			// 今回のターンで「書き込める最大サイズ」を計算する
			int availableSpace = BUFFER_SIZE - size;
			int writeLen = Math.min(remainBytes, availableSpace);

			// バッファの head から末尾までの直線距離を計算（配列の端の折り返し対策）
			int bytesToEnd = BUFFER_SIZE - head;
			if (writeLen <= bytesToEnd) {
				System.arraycopy(data, srcPos, rawBuffer, head, writeLen);
				head = (head + writeLen) & BUFFER_MASK;
			} else {
				System.arraycopy(data, srcPos, rawBuffer, head, bytesToEnd);
				int overflowLen = writeLen - bytesToEnd;
				System.arraycopy(data, srcPos + bytesToEnd, rawBuffer, 0, overflowLen);
				head = overflowLen;
			}

			// 各種ポインタ・サイズの更新
			size += writeLen;
			srcPos += writeLen;
			remainBytes -= writeLen;

			notifyAll();
		}
	}

	/**
	 * 先頭3840バイトを読み込む<br>
	 * 読み込める程バッファがたまっていない場合、nullを返す。
	 */
	public synchronized byte[] get() {
		if (size < OPUS_FRAME_LENGTH) {
			return null;
		}

		byte[] frameData = new byte[OPUS_FRAME_LENGTH];

		int bytesToEnd = BUFFER_SIZE - tail;
		if (OPUS_FRAME_LENGTH <= bytesToEnd) {
			System.arraycopy(rawBuffer, tail, frameData, 0, OPUS_FRAME_LENGTH);
			tail = (tail + OPUS_FRAME_LENGTH) & BUFFER_MASK;
		} else {
			System.arraycopy(rawBuffer, tail, frameData, 0, bytesToEnd);
			int overflowLen = OPUS_FRAME_LENGTH - bytesToEnd;
			System.arraycopy(rawBuffer, 0, frameData, bytesToEnd, overflowLen);
			tail = overflowLen;
		}

		size -= OPUS_FRAME_LENGTH;

		notifyAll();

		return frameData;
	}

	public synchronized int size() {
		return size;
	}

	/**
	 * 切断時などにバッファを完全に空にする
	 */
	public synchronized void clear() {
		head = 0;
		tail = 0;
		size = 0;
		notifyAll();
	}
}
