package tokyo.archangel.sdb.internal.servicies.transportcrypter;

public interface TransportCryptService {
	/**
	 * トランスポート暗号化を行う
	 * @param rtpHeader 12バイトのRTPヘッダー
	 * @param opusPayload 生のOpus音声データ
	 * @return Discordにそのまま送信できるUDPパケットデータ
	 */
	public byte[] encryptPacket(byte[] rtpHeader, byte[] opusPayload);
}