package tokyo.archangel.sdb.internal.servicies.opcode.gateway;

import tokyo.archangel.sdb.internal.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

/**
 * opcodeを受け取った際の処理を担当するインターフェース
 */
public interface GatewayOpcodeServiceInterface {
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
