package tokyo.archangel.sdb.discord.dto.gateway;

import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

public class NotImplementCodeDto extends OpCodeBaseDto{

	public NotImplementCodeDto(GatewayOpCode opCode, DispatchEvent eventName, Long sequence) {
		super(opCode, eventName, sequence);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public String getServiceClassName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
