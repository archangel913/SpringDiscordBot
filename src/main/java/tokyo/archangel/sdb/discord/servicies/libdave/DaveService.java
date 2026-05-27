package tokyo.archangel.sdb.discord.servicies.libdave;

import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;

public interface DaveService {

	/**
	 * 初期化処理
	 * @param channelId
	 * @param userId
	 * @param ssrc
	 * @param channelInfo
	 */
	public void init(String channelId, String userId, int ssrc, VoiceChannelInfo channelInfo);

	/**
	 * 外部送信者を処理する
	 * @param data
	 */
	public void processExternalSender(byte[] data);

	/**
	 * 提案を処理する
	 * @param data 提案バイナリ
	 * @return 提案処理で生成されたcommitバイナリ
	 * @throws IllegalStateException 提案の処理に失敗したとき
	 */
	public byte[] processProposals(byte[] data) throws IllegalStateException;

	/**
	 * コミットを処理する
	 * @param data コミットバイナリ
	 * @throws IllegalStateException コミットの処理に失敗したとき
	 */
	public void processCommit(byte[] data) throws IllegalStateException;

	/**
	 * ウェルカムを処理する
	 * @param data ウェルカムバイナリ
	 * @throws IllegalStateException ウェルカムの処理に失敗したとき
	 */
	public void processWelcome(byte[] data) throws IllegalStateException;

	/**
	 * キーパッケージを取得する
	 * @return キーパッケージ
	 * @throws IllegalStateException キーパッケージの取得に失敗したとき
	 */
	public byte[] getMarshalledKeyPackage() throws IllegalStateException;

	/**
	 * キーラチェットを更新する
	 * @throws IllegalStateException キーラチェットの更新に失敗したとき
	 */
	public void updateEncryptorRachet() throws IllegalStateException;

	/**
	 * 暗号化を行う
	 * @param opusData 暗号化対象バイナリ
	 * @return 暗号化後バイナリ
	 * @throws IllegalStateException 暗号化に失敗したとき
	 */
	public byte[] encryptOpus(byte[] opusData) throws IllegalStateException;

	/**
	 * サービスを閉じる
	 */
	public void close();
}
