package tokyo.archangel.sdb.discord.dto.gateway.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.enumeration.DispatchEvent;
import tokyo.archangel.sdb.discord.enumeration.GatewayOpCode;

@Value
@EqualsAndHashCode(callSuper = true)
public class Code2Dto extends OpCodeBaseDto {
	@JsonProperty("d")
	private Detail detail;

	public Code2Dto(Detail detail, DispatchEvent eventName, Long sequence) {
		super(GatewayOpCode.IDENTIFY, eventName, sequence);
		this.detail = detail;
	}

	@Override
	public String getServiceClassName() {
		// データの送信用のため、対応するサービスなし
		return null;
	}
}
