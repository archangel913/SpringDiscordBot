package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

/**
 * ハートビート送信サービスを提供するプロバイダー
 */
@Service
@Slf4j
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
	
	/**
	 * ハートビート送信用サービスを削除する。
	 * @param session
	 */
	public void removeService(WebSocketSession session) {
		heartBeatServices.remove(session.getId());
		log.debug("ハートビートサービスを削除しました。現在有効なサービスは" + heartBeatServices.size() + "個です");
	}
}
