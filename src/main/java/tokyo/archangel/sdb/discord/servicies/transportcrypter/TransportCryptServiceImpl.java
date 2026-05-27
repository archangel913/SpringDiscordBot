package tokyo.archangel.sdb.discord.servicies.transportcrypter;

import java.nio.ByteBuffer;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/**
 * トランスポート暗号化を行うためのクラス<br>
 * 現在aead_aes256_gcm_rtpsizeのみ対応
 */
@Service
public class TransportCryptServiceImpl implements TransportCryptService {
	// WebSocket(OPCODE 4等)で取得した32バイトの鍵
	private byte[] secretKey = null;
	private int nonce = 0;

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
			// 1. Nonceの準備 (12バイト、先頭4バイトがカウンター)
			byte[] nonceBytes = new byte[12];

			// カウンターをインクリメント（Pythonの self._nonce += 1 に相当）
			// ※溢れた場合はJavaのintの挙動で自動的にループ、または手動でマスクしてもOK
			nonce++;

			// Big Endianで先頭4バイトに書き込む
			nonceBytes[0] = (byte) (nonce >> 24);
			nonceBytes[1] = (byte) (nonce >> 16);
			nonceBytes[2] = (byte) (nonce >> 8);
			nonceBytes[3] = (byte) nonce;

			// Pythonの nonce_padding = nonce[:4] に相当
			byte[] noncePadding = new byte[4];
			System.arraycopy(nonceBytes, 0, noncePadding, 0, 4);

			// 2. AES-GCMによる暗号化
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonceBytes); // 128bit = 16バイトのタグ
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

			// RTPヘッダーをAADとしてセット
			cipher.updateAAD(rtpHeader);

			// 暗号化を実行 (Javaの doFinal は「暗号文 + 16バイトの認証タグ」を返します)
			byte[] encryptedPayload = cipher.doFinal(opusPayload);

			// 3. パケットの組み立て
			// 全体長 = RTPヘッダー(12) + 暗号文&タグ(encryptedPayload) + カウンター(4)
			ByteBuffer packet = ByteBuffer.allocate(rtpHeader.length + encryptedPayload.length + noncePadding.length);

			packet.put(rtpHeader); // 先頭にRTPヘッダー
			packet.put(encryptedPayload); // 続いて暗号化されたペイロード（タグ付き）
			packet.put(noncePadding); // 末尾に4バイトのカウンターパディング

			return packet.array();

		} catch (Exception e) {
			return null;
		}
	}
}
