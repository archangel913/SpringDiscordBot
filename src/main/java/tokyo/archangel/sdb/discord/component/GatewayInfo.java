package tokyo.archangel.sdb.discord.component;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.ready.ReadyDetail;

@Getter
@Setter
@Component
public class GatewayInfo {
	private Long sequence;
	
	private ReadyDetail readyDetail;

}
