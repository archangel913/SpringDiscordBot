package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

/**
 * ハートビート送信サービスを提供するプロバイダー
 */
@Service
public class HeartBeatServiceProvider {
	@Autowired
	private ObjectProvider<HeartBeatServiceImpl> serviceProvider;

	private ConcurrentMap<String, HeartBeatServiceImpl> heartBeatServices = new ConcurrentHashMap<>();

	/**
	 * ハートビート送信サービスを取得する
	 * @param session
	 * @return
	 */
	public HeartBeatService getHeartBeatService(WebSocketSession session) {
		return heartBeatServices.computeIfAbsent(session.getId(), id -> {
			return serviceProvider.getObject();
		});
	}
}
