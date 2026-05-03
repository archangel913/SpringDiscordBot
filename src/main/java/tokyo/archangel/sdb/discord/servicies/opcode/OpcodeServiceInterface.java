package tokyo.archangel.sdb.discord.servicies.opcode;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

/**
 * opcodeを受け取った際の処理を担当するインターフェース
 */
public interface OpcodeServiceInterface {
	/**
	 * サービスの処理を実行
	 * @param dto
	 */
	public void exec(OpCodeReceiveBaseDto dto);
	
	/**
	 * メッセージ送信用のクラスを渡す
	 * @param sendMessageService
	 */
	public void setSendSessageService(SendMessageService sendMessageService);
}
