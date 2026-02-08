package tokyo.archangel.sdb.discord.servicies.gateway.opcode;

import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;

public interface OpcodeServiceInterface {
	/** サービスの処理を実行 */
	public void exec(OpCodeReceiveBaseDto dto);
}
