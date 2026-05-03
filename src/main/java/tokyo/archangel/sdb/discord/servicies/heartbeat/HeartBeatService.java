package tokyo.archangel.sdb.discord.servicies.heartbeat;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeSendBaseDto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

public interface HeartBeatService {
	/**
	 * 送信するopcodeを設定する
	 * @param opcodeClassName
	 */
	public void setSendOpcode(String opcodeClassName);
	
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
	public void sendHeartBeat(OpCodeSendBaseDto dto);
	
	/**
	 * ハートビートを終了する
	 */
	public void stopHeartBeat();
}
