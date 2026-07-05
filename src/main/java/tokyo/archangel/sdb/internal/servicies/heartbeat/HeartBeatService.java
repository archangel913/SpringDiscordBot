package tokyo.archangel.sdb.internal.servicies.heartbeat;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.internal.component.voice.VoiceChannelInfo;

public interface HeartBeatService {
	/**
	 * VoiceChannelInfoを設定する<br>
	 * nullの場合、ゲートウェイ扱いとなる
	 * @param opcodeClassName
	 */
	public void setVoiceChannelInfo(VoiceChannelInfo voiceChannelInfo);

	/**
	 * メッセージサービスを設定する
	 * @param sendMessageService
	 */
	public void setSendMessageService(WebSocketSession session);

	/**
	 * 処理を実行する
	 * @param interval ハートビート送信間隔
	 * @param channelId チャンネルID。gatewayの場合は"gateway"
	 * @return CompletableFuture
	 */
	public CompletableFuture<Void> exec(int interval, String channelId);

	/**
	 * ハートビートの応答を受け取ったことを通知する
	 */
	public void receiveAck();

	/**
	 * ハートビートの送信を行う
	 */
	public void sendHeartBeat(String json);

	/**
	 * ハートビートを終了する
	 */
	public void close();
}
