package tokyo.archangel.sdb.internal.servicies.libdave;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

/**
 * E2EE暗号化ライブラリlibdaveを使用するためのインターフェース
 */
public interface LibDave extends Library {
	LibDave INSTANCE = Native.load("libdave", LibDave.class);
	
	// ==========================================
	// 定数定義
	// ==========================================
    int DAVE_CODEC_UNKNOWN = 0;
    int DAVE_CODEC_OPUS = 1;
    int DAVE_CODEC_VP8 = 2;
    int DAVE_CODEC_VP9 = 3;
    int DAVE_CODEC_H264 = 4;
    int DAVE_CODEC_H265 = 5;
    int DAVE_CODEC_AV1 = 6;

    int DAVE_MEDIA_TYPE_AUDIO = 0;
    int DAVE_MEDIA_TYPE_VIDEO = 1;

    int DAVE_ENCRYPTOR_RESULT_CODE_SUCCESS = 0;
    int DAVE_ENCRYPTOR_RESULT_CODE_ENCRYPTION_FAILURE = 1;
    int DAVE_ENCRYPTOR_RESULT_CODE_MISSING_KEY_RATCHET = 2;
    int DAVE_ENCRYPTOR_RESULT_CODE_MISSING_CRYPTOR = 3;
    int DAVE_ENCRYPTOR_RESULT_CODE_TOO_MANY_ATTEMPTS = 4;

    int DAVE_DECRYPTOR_RESULT_CODE_SUCCESS = 0;
    int DAVE_DECRYPTOR_RESULT_CODE_DECRYPTION_FAILURE = 1;
    int DAVE_DECRYPTOR_RESULT_CODE_MISSING_KEY_RATCHET = 2;
    int DAVE_DECRYPTOR_RESULT_CODE_INVALID_NONCE = 3;
    int DAVE_DECRYPTOR_RESULT_CODE_MISSING_CRYPTOR = 4;

    int DAVE_LOGGING_SEVERITY_VERBOSE = 0;
    int DAVE_LOGGING_SEVERITY_INFO = 1;
    int DAVE_LOGGING_SEVERITY_WARNING = 2;
    int DAVE_LOGGING_SEVERITY_ERROR = 3;
    int DAVE_LOGGING_SEVERITY_NONE = 4;

	// ==========================================
	// 構造体定義 (インナークラスとして内包)
	// ==========================================
	@Structure.FieldOrder({
			"passthroughCount", "encryptSuccessCount", "encryptFailureCount",
			"encryptDuration", "encryptAttempts", "encryptMaxAttempts", "encryptMissingKeyCount"
	})
	class DAVEEncryptorStats extends Structure {
		public long passthroughCount;
		public long encryptSuccessCount;
		public long encryptFailureCount;
		public long encryptDuration;
		public long encryptAttempts;
		public long encryptMaxAttempts;
		public long encryptMissingKeyCount;
	}

	@Structure.FieldOrder({
			"passthroughCount", "decryptSuccessCount", "decryptFailureCount",
			"decryptDuration", "decryptAttempts", "decryptMissingKeyCount", "decryptInvalidNonceCount"
	})
	class DAVEDecryptorStats extends Structure {
		public long passthroughCount;
		public long decryptSuccessCount;
		public long decryptFailureCount;
		public long decryptDuration;
		public long decryptAttempts;
		public long decryptMissingKeyCount;
		public long decryptInvalidNonceCount;
	}

	// ==========================================
	// コールバック定義
	// ==========================================
	interface DAVEMLSFailureCallback extends Callback {
		void invoke(String source, String reason, Pointer userData);
	}

	interface DAVEPairwiseFingerprintCallback extends Callback {
		void invoke(Pointer fingerprint, NativeLong length, Pointer userData);
	}

	interface DAVEEncryptorProtocolVersionChangedCallback extends Callback {
		void invoke(Pointer userData);
	}

	interface DAVELogSinkCallback extends Callback {
		void invoke(int severity, String file, int line, String message);
	}

	// ==========================================
	// C関数マッピング (size_t は NativeLong で代用)
	// ==========================================

	// --- Version ---
	short daveMaxSupportedProtocolVersion();

	// --- Memory Management ---
	void daveFree(Pointer ptr);

	// --- Session Management ---
	
	/**
	 * MLSセッション作成
	 * @param context
	 * @param authSessionId
	 * @param callback
	 * @param userData
	 * @return
	 */
	Pointer daveSessionCreate(Pointer context, String authSessionId, DAVEMLSFailureCallback callback, Pointer userData);

	void daveSessionDestroy(Pointer session);

	/**
	 * MLSセッション初期化
	 * @param session 初期化対象のポインター
	 * @param version バージョン(通常は1)
	 * @param groupId チャンネルID
	 * @param selfUserId 自身のユーザーID
	 */
	void daveSessionInit(Pointer session, short version, long groupId, String selfUserId);

	void daveSessionReset(Pointer session);

	void daveSessionSetProtocolVersion(Pointer session, short version);

	short daveSessionGetProtocolVersion(Pointer session);

	void daveSessionGetLastEpochAuthenticator(Pointer session, PointerByReference authenticator, Pointer length);

	/**
	 * 外部送信者のバイナリを処理する(opcode 25)<br>
	 * 送られてきたバイナリの先頭1バイトが25のものが対象<br>
	 * 先頭1バイトを削って残りのバイナリを流し込む
	 * @param session 処理対象のMLSセッション
	 * @param externalSender 送られてきた外部送信者情報バイナリ
	 * @param length 送られてきたバイナリの長さ(先頭1バイトを削った長さ)
	 */
	void daveSessionSetExternalSender(Pointer session, byte[] externalSender, NativeLong length);

	/**
	 * proposalsのバイナリを処理する(pocode 27)<br>
	 * 送られてきたバイナリの先頭1バイトが27のものが対象<br>
	 * 先頭1バイトを削って残りのバイナリを流し込む
	 * @param session 処理対象のMLSセッション
	 * @param proposals 送られてきたproposalsバイナリ
	 * @param length 送られてきたバイナリの長さ(先頭1バイトを削った長さ)
	 * @param recognizedUserIds 現在チャンネルに参加しているユーザーID一覧
	 * @param recognizedUserIdsLength 現在チャンネルに参加しているユーザーIDの個数
	 * @param commitWelcomeBytes 生成されたwelcomeバイナリ
	 * @param commitWelcomeBytesLength 生成されたwelcomeバイナリの長さ
	 */
	void daveSessionProcessProposals(Pointer session, byte[] proposals, NativeLong length, Pointer recognizedUserIds,
			NativeLong recognizedUserIdsLength, PointerByReference commitWelcomeBytes,
			Pointer commitWelcomeBytesLength);

	/**
	 * コミット処理を行う(pocode 29)<br>
	 * 送られてきたバイナリの先頭1バイトが29のものが対象<br>
	 * 先頭1バイトを削って残りのバイナリを流し込む
	 * @param session 処理対象のMLSセッション
	 * @param commit 送られてきたcommitバイナリ
	 * @param length 送られてきたバイナリの長さ(先頭1バイトを削った長さ)
	 * @return
	 */
	Pointer daveSessionProcessCommit(Pointer session, byte[] commit, NativeLong length);

	/**
	 * welcomeのバイナリを処理する(pocode 30)<br>
	 * 送られてきたバイナリの先頭1バイトが27のものが対象<br>
	 * <b>先頭1バイトでは無く、3バイトを削って</b>残りのバイナリを流し込む
	 * @param session 処理対象のMLSセッション
	 * @param welcome 送られてきたwelcomeバイナリ
	 * @param length 送られてきたバイナリの長さ(先頭3バイトを削った長さ)
	 * @param recognizedUserIds 現在チャンネルに参加しているユーザーID一覧
	 * @param recognizedUserIdsLength 現在チャンネルに参加しているユーザーIDの個数
	 * @return
	 */
	Pointer daveSessionProcessWelcome(Pointer session, Pointer welcome, NativeLong length, Pointer recognizedUserIds,
			NativeLong recognizedUserIdsLength);

	/**
	 * キーパッケージを生成する<br>
	 * 外部送信者を処理後、この関数で生成されたバイト列をdiscordへ送り返さなければならない<br>
	 * 返信opcodeは26<br>
	 * 生成されたバイト列の先頭1バイトを付与して送り返す
	 * @param session 処理対象のMLSセッション
	 * @param keyPackage 生成されたキーパッケージ
	 * @param length 生成されたキーパッケージの長さ
	 */
	void daveSessionGetMarshalledKeyPackage(Pointer session, PointerByReference keyPackage, Pointer length);

	/**
	 * キーラチェットを取得する<br>
	 * このキーラチェットを使用して暗号化、復号化を行う
	 * @param session 処理対象のMLSセッション
	 * @param userId 送信された・送信するユーザーID
	 * @return
	 */
	Pointer daveSessionGetKeyRatchet(Pointer session, String userId);

	void daveSessionGetPairwiseFingerprint(Pointer session, short version, String userId,
			DAVEPairwiseFingerprintCallback callback, Pointer userData);

	// --- Key Ratchet ---
	/**
	 * キーラチェットを開放する
	 * @param keyRatchet 処理対象のキーラチェット
	 */
	void daveKeyRatchetDestroy(Pointer keyRatchet);

	// --- Commit Result ---
	boolean daveCommitResultIsFailed(Pointer commitResultHandle);

	boolean daveCommitResultIsIgnored(Pointer commitResultHandle);

	void daveCommitResultGetRosterMemberIds(Pointer commitResultHandle, PointerByReference rosterIds,
			Pointer rosterIdsLength);

	void daveCommitResultGetRosterMemberSignature(Pointer commitResultHandle, long rosterId,
			PointerByReference signature, Pointer signatureLength);

	/**
	 * コミットリザルトのメモリ開放
	 * @param commitResultHandle 処理対象のコミットリザルト
	 */
	void daveCommitResultDestroy(Pointer commitResultHandle);

	// --- Welcome Result ---
	void daveWelcomeResultGetRosterMemberIds(Pointer welcomeResultHandle, PointerByReference rosterIds,
			Pointer rosterIdsLength);

	void daveWelcomeResultGetRosterMemberSignature(Pointer welcomeResultHandle, long rosterId,
			PointerByReference signature, Pointer signatureLength);

	/**
	 * ウェルカムリザルトのメモリ開放
	 * @param welcomeResultHandle 処理対象のウェルカムリザルト
	 */
	void daveWelcomeResultDestroy(Pointer welcomeResultHandle);

	// --- Encryptor ---
	
	/**
	 * 暗号化器作成
	 * @return
	 */
	Pointer daveEncryptorCreate();

	/**
	 * 暗号化器メモリ開放
	 * @param encryptor 処理対象の暗号化器
	 */
	void daveEncryptorDestroy(Pointer encryptor);

	/**
	 * キーラチェットを設定する<br>
	 * キーラチェットが変わるたびにこの関数で設定しなおす必要がある
	 * @param encryptor 処理対象の暗号化器
	 * @param keyRatchet 設定するキーラチェット
	 */
	void daveEncryptorSetKeyRatchet(Pointer encryptor, Pointer keyRatchet);

	void daveEncryptorSetPassthroughMode(Pointer encryptor, boolean passthroughMode);

	/**
	 * 対象の暗号化器にssrcとコーデックをセット
	 * @param encryptor 処理対象のMLSセッション
	 * @param ssrc アサインするssrc
	 * @param codecType コーデックの種類
	 */
	void daveEncryptorAssignSsrcToCodec(Pointer encryptor, int ssrc, int codecType);

	short daveEncryptorGetProtocolVersion(Pointer encryptor);

	NativeLong daveEncryptorGetMaxCiphertextByteSize(Pointer encryptor, int mediaType, NativeLong frameSize);

	boolean daveEncryptorHasKeyRatchet(Pointer encryptor);

	boolean daveEncryptorIsPassthroughMode(Pointer encryptor);

	/**
	 * 暗号化を行う
	 * @param encryptor 暗号化を行う暗号化器
	 * @param mediaType メディアタイプ
	 * @param ssrc ssrc
	 * @param frame 暗号化対象の生バイナリ
	 * @param frameLength 暗号化対象の生バイナリの長さ
	 * @param encryptedFrame 暗号化後のバイナリ
	 * @param encryptedFrameCapacity 暗号化後のバイナリの最長の長さ
	 * @param bytesWritten 実際に暗号化されたバイナリの長さ
	 * @return
	 */
	int daveEncryptorEncrypt(Pointer encryptor, int mediaType, int ssrc, byte[] frame, NativeLong frameLength,
			byte[] encryptedFrame, NativeLong encryptedFrameCapacity, PointerByReference bytesWritten);

	void daveEncryptorSetProtocolVersionChangedCallback(Pointer encryptor,
			DAVEEncryptorProtocolVersionChangedCallback callback, Pointer userData);

	void daveEncryptorGetStats(Pointer encryptor, int mediaType, DAVEEncryptorStats stats);

	// --- Decryptor ---
	Pointer daveDecryptorCreate();

	void daveDecryptorDestroy(Pointer decryptor);

	void daveDecryptorTransitionToKeyRatchet(Pointer decryptor, Pointer keyRatchet);

	void daveDecryptorTransitionToPassthroughMode(Pointer decryptor, boolean passthroughMode);

	int daveDecryptorDecrypt(Pointer decryptor, int mediaType, byte[] encryptedFrame, NativeLong encryptedFrameLength,
			byte[] frame, NativeLong frameCapacity, PointerByReference bytesWritten);

	NativeLong daveDecryptorGetMaxPlaintextByteSize(Pointer decryptor, int mediaType, NativeLong encryptedFrameSize);

	void daveDecryptorGetStats(Pointer decryptor, int mediaType, DAVEDecryptorStats stats);

	// --- Logging ---
	void daveSetLogSinkCallback(DAVELogSinkCallback callback);
}
