package tokyo.archangel.sdb.discord.servicies.libdave;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.PointerByReference;

import jakarta.annotation.PreDestroy;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;

/**
 * E2EE暗号化を行うためのクラス
 */
@Service
@Scope("prototype")
public class DaveServiceImpl implements DaveService {
	Pointer session;

	Pointer encryptor;

	// TODO 復号対応
	Pointer decryptor;

	String userId;

	int ssrc;

	VoiceChannelInfo channelInfo;

	private PointerByReference keyPackageRef;

	@Override
	public synchronized void init(String channelId, String userId, int ssrc, VoiceChannelInfo channelInfo) {
		this.channelInfo = channelInfo;
		this.userId = userId;
		this.ssrc = ssrc;

		// セッション作成
		session = LibDave.INSTANCE.daveSessionCreate(Pointer.NULL, null, null, Pointer.NULL);
		LibDave.INSTANCE.daveSessionInit(session, (short) 1, Long.valueOf(channelId), userId);

		// 暗号化器作成
		encryptor = LibDave.INSTANCE.daveEncryptorCreate();
		// SSRCにOpus(1)をアサイン
		LibDave.INSTANCE.daveEncryptorAssignSsrcToCodec(encryptor, ssrc, LibDave.DAVE_CODEC_OPUS);
	}

	@Override
	public synchronized void processExternalSender(byte[] data) {
		LibDave.INSTANCE.daveSessionSetExternalSender(session, data, new NativeLong(data.length));
	}

	@Override
	public synchronized byte[] processProposals(byte[] data) {
		List<String> userIds = channelInfo.getJoinedUserIds();
		String[] recognizedUsers = userIds.toArray(new String[userIds.size()]);

		StringArray cStyleUserArray = new StringArray(recognizedUsers, "UTF-8");

		Memory bytesMem = new Memory(600 * recognizedUsers.length);
		PointerByReference bytesWrittenRef = new PointerByReference(bytesMem);

		Memory bytesLenMem = new Memory(Native.POINTER_SIZE);
		bytesLenMem.setLong(0, 0);
		LibDave.INSTANCE.daveSessionProcessProposals(session, data, new NativeLong(data.length), cStyleUserArray,
				new NativeLong(recognizedUsers.length), bytesWrittenRef, bytesLenMem);

		int actualLength = bytesLenMem.getInt(0);
		if (actualLength <= 0) {
			throw new IllegalStateException("proposalsの処理にに失敗しました。ssrc:" + ssrc);
		}

		return bytesWrittenRef.getValue().getByteArray(0, actualLength);
	}

	@Override
	public synchronized void processCommit(byte[] data) {
		Pointer commitResult = LibDave.INSTANCE.daveSessionProcessCommit(session, data, new NativeLong(data.length));

		if (commitResult == null) {
			throw new IllegalStateException("commitの処理に失敗しました。ssrc:" + ssrc);
		}

		LibDave.INSTANCE.daveCommitResultDestroy(commitResult);
	}

	@Override
	public synchronized void processWelcome(byte[] data) {
		List<String> userIds = channelInfo.getJoinedUserIds();
		String[] recognizedUsers = userIds.toArray(new String[userIds.size()]);

		StringArray cStyleUserArray = new StringArray(recognizedUsers, "UTF-8");

		Memory nativeBuffer = new Memory(data.length);
		nativeBuffer.write(0, data, 0, data.length);

		Pointer welcomeResult = LibDave.INSTANCE.daveSessionProcessWelcome(
				session, nativeBuffer, new NativeLong(data.length), cStyleUserArray,
				new NativeLong(recognizedUsers.length));

		if (welcomeResult == null) {
			throw new IllegalStateException("welcomeの処理に失敗しました。ssrc:" + ssrc);
		}

		LibDave.INSTANCE.daveWelcomeResultDestroy(welcomeResult);
	}

	@Override
	public synchronized byte[] getMarshalledKeyPackage() {
		keyPackageRef = new PointerByReference();
		Memory lengthMem = new Memory(Native.POINTER_SIZE);
		lengthMem.setLong(0, 0);

		LibDave.INSTANCE.daveSessionGetMarshalledKeyPackage(session, keyPackageRef, lengthMem);

		long actualLength = lengthMem.getLong(0);
		Pointer keyPackagePtr = keyPackageRef.getValue();

		if (keyPackagePtr == null || actualLength <= 0) {
			throw new IllegalStateException("KeyPackageの取得に失敗しました。ssrc:" + ssrc);
		}

		return keyPackagePtr.getByteArray(0, (int) actualLength);
	}

	@Override
	public synchronized void updateEncryptorRachet() {
		// セッションから自分用の鍵（KeyRatchet）を抽出
		Pointer ratchet = LibDave.INSTANCE.daveSessionGetKeyRatchet(session, userId);
		if (ratchet == Pointer.NULL) {
			throw new IllegalStateException("KeyRatchetの抽出に失敗しました。ssrc:" + ssrc);
		}

		// 暗号化器に抽出した鍵をセット
		LibDave.INSTANCE.daveEncryptorSetKeyRatchet(encryptor, ratchet);

		// 取得したラチェットハンドル自体を解放
		LibDave.INSTANCE.daveKeyRatchetDestroy(ratchet);
	}

	@Override
	public synchronized byte[] encryptOpus(byte[] opusData) {
		if (encryptor == Pointer.NULL) {
			return null;
		}

		byte[] outBuffer = new byte[opusData.length + 256];

		Memory mem = new Memory(Native.POINTER_SIZE);
		PointerByReference bytesWrittenRef = new PointerByReference(mem);

		// メディアタイプ: 0 (Audio), SSRC, 入力データ, 出力バッファを指定
		int result = LibDave.INSTANCE.daveEncryptorEncrypt(
				encryptor, LibDave.DAVE_MEDIA_TYPE_AUDIO, ssrc, opusData, new NativeLong(opusData.length), outBuffer,
				new NativeLong(outBuffer.length), bytesWrittenRef);

		if (result != 0) {
			// ここが2になる場合は、まだprocessWelcomePacketが呼ばれていないか成功していません
			throw new IllegalStateException("暗号化に失敗しました。ssrc:" + ssrc);
		}

		long actualLength = bytesWrittenRef.getPointer().getNativeLong(0).longValue();
		byte[] actualPacket = new byte[(int) actualLength];
		System.arraycopy(outBuffer, 0, actualPacket, 0, actualPacket.length);
		return actualPacket;
	}

	@PreDestroy
	@Override
	public synchronized void close() {
		if (encryptor != Pointer.NULL) {
			LibDave.INSTANCE.daveEncryptorDestroy(encryptor);
			encryptor = Pointer.NULL;
		}
		if (session != Pointer.NULL) {
			LibDave.INSTANCE.daveSessionDestroy(session);
			session = Pointer.NULL;
		}
	}
}
