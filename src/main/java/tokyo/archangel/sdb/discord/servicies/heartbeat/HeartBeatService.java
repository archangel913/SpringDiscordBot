package tokyo.archangel.sdb.discord.servicies.heartbeat;

import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

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
	public void setSendMessageService(SendMessageService sendMessageService);
	
	/**
	 * 処理を実行する
	 * @param interval
	 */
	public void exec(int interval);
	
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
	public void stopHeartBeat();
}
