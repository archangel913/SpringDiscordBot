package tokyo.archangel.sdb.discord.enumeration;

import java.util.List;

public enum Speaking {
	/**
	 * 音声オーディオの通常伝送<br>
	 * 1 << 0
	 */
	MICROPHONE(1),

	/**
	 * ビデオのコンテキスト音声の送信、発話表示なし<br>
	 * 1 << 1
	 */
	SOUNDSHARE(2),

	/**
	 * 優先スピーカー、他のスピーカーの音量を下げる<br>
	 * 1 << 2
	 */
	PRIORITY(4);

	private final int speaking;

	private Speaking(int speaking) {
		this.speaking = speaking;
	}

	public int getValue() {
		return speaking;
	}

	/**
	 * 引数のインテントを含んだフラグを作成する
	 * @param intents
	 * @return
	 */
	public static int buildFlag(List<Speaking> intents) {
		int result = 0;
		for (Speaking i : intents) {
			result = result | i.getValue();
		}
		return result;
	}

	/**
	 * すべてのインテントを含んだフラグを作成する
	 * @return
	 */
	public static int buildAllFlag() {
		int result = 0;
		for (Speaking i : Speaking.values()) {
			result = result | i.getValue();
		}
		return result;
	}
}
