package tokyo.archangel.sdb.discord.servicies.heartbeat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.socket.WebSocketSession;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * ハートビート送信サービスを提供するプロバイダー
 */
@Slf4j
public class HeartBeatServiceProvider {
	private ObjectProvider<HeartBeatServiceImpl> serviceProvider;

	private ConcurrentMap<String, HeartBeatServiceImpl> heartBeatServices = new ConcurrentHashMap<>();

	public HeartBeatServiceProvider(ObjectProvider<HeartBeatServiceImpl> serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

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
		HeartBeatService heartBeatService = heartBeatServices.remove(session.getId());
		heartBeatService.close();
		log.debug("ハートビートサービスを削除しました。現在有効なサービスは{}個です", heartBeatServices.size());
	}

	@PreDestroy
	public void close() {
		heartBeatServices.forEach((id, service) -> {
			if (service != null) {
				service.close();
			}
		});
		heartBeatServices.clear();
	}
}
