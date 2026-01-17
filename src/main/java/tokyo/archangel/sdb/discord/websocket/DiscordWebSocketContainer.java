package tokyo.archangel.sdb.discord.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class DiscordWebSocketContainer {
	private Map<String, WebSocketSession> sessions;
	
	/**
	 * セッションをコンテナに登録する
	 * @param session
	 */
	public void addSession(WebSocketSession session) {
		sessions.put(session.getId(), session);
	}
	
	/**
	 * セッションをコンテナから取得する
	 * @param id
	 * @return WebSocketSession ただし、取得できなかった場合はnull
	 */
	public WebSocketSession getSession(String id) {
		return sessions.get(id);
	}
	
	/**
	 * セッションをコンテナから削除する
	 * @param id
	 */
	public void removeSession(String id) {
		sessions.remove(id);
	}
	
	/**
	 * 複数のセッションを取得する
	 * @param ids
	 * @return 取得できたセッションのリスト 取得できなかったものはリストに入らない
	 */
	public List<WebSocketSession> getSessions(List<String> ids){
		List<WebSocketSession> targets = new ArrayList<WebSocketSession>();
		for(String id : ids) {
			WebSocketSession session = getSession(id);
			if(Objects.nonNull(session)) {
				targets.add(session);
			}
		}
		return targets;
	}
	
	/**
	 * 複数のセッションを削除する
	 * @param ids
	 */
	public void removeSessions(List<String> ids){
		for(String id : ids) {
			removeSession(id);
		}
	}
}
