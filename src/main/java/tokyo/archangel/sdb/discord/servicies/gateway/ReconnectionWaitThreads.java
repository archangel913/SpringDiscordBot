package tokyo.archangel.sdb.discord.servicies.gateway;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ReconnectionWaitThreads {
	// もうちょいこのclassなんとかならん？
	// GatewayReconnectionServiceから循環参照が発生しているこのクラスに責務を集約できないか
	// heartbeatのスレッド本体を持たせて、サービスとの依存性を消せないか
	private List<GatewayReconnectionService> reconnectionServices = new ArrayList<GatewayReconnectionService>();

	public void add(GatewayReconnectionService service) {
		reconnectionServices.add(service);
	}

	public GatewayReconnectionService remove() {
		return reconnectionServices.remove(0);
	}
}
