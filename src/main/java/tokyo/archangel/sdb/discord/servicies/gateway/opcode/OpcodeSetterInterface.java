package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;

/**
 * フィールドに値をセットする用<br>
 * オペコードのサービスクラスでは実装してください。キャストエラーが発生します。<br>
 * {@link OpcodeServiceFactory} 以外では使用しないでください。<br>
 * 代わりに {@link OpcodeServiceInterface} を使用してください。
 */
@Deprecated
public interface OpcodeSetterInterface {
	/** セッションをセットする */
	public void setSession(WebSocketSession session);
	
	/** daoをセットする */
	public void setDto(OpCodeBaseDto dto);
}
