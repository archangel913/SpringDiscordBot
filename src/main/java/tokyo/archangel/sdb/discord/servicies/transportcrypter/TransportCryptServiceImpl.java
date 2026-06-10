package tokyo.archangel.sdb.discord.servicies.transportcrypter;

import java.nio.ByteBuffer;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * トランスポート暗号化を行うためのクラス<br>
 * 現在aead_aes256_gcm_rtpsizeのみ対応
 */
public class TransportCryptServiceImpl implements TransportCryptService {
	private byte[] secretKey = null;
	private int nonce = 0;
	private byte[] nonceBytes = new byte[12];

	private final ByteBuffer packetBuffer = ByteBuffer.allocate(4096);

	public void setKey(byte[] secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @param rtpHeader 12バイトのRTPヘッダー
	 * @param opusPayload 生のOpus音声データ
	 * @return Discordにそのまま送信できるUDPパケットデータ
	 */
	@Override
	public byte[] encryptPacket(byte[] rtpHeader, byte[] opusPayload) {
		try {
			// カウンターをインクリメント（Pythonの self._nonce += 1 に相当）
			// ※溢れた場合はJavaのintの挙動で自動的にループ、または手動でマスクしてもOK
			nonce++;

			// Big Endianで先頭4バイトに書き込む
			nonceBytes[0] = (byte) (nonce >> 24);
			nonceBytes[1] = (byte) (nonce >> 16);
			nonceBytes[2] = (byte) (nonce >> 8);
			nonceBytes[3] = (byte) nonce;

			// 2. AES-GCMによる暗号化
			// TODO パフォーマンスチューニング(getInstanceが重い。ThreadLocalを検討)
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonceBytes); // 128bit = 16バイトのタグ
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

			// RTPヘッダーをAADとしてセット
			cipher.updateAAD(rtpHeader);

			// 暗号化を実行 (Javaの doFinal は「暗号文 + 16バイトの認証タグ」を返します)
			byte[] encryptedPayload = cipher.doFinal(opusPayload);

			// 3. パケットの組み立て
			packetBuffer.clear();
			packetBuffer.put(rtpHeader);
			packetBuffer.put(encryptedPayload);
			packetBuffer.put(nonceBytes, 0, 4);

			packetBuffer.flip();
			byte[] result = new byte[packetBuffer.limit()];
			packetBuffer.get(result);
			return result;

		} catch (Exception e) {
			return null;
		}
	}
}
