package tokyo.archangel.sdb.discord.servicies.sendMessage;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.ApplicationProperties;

/**
 * メッセージ送信用サービスを提供するクラス
 */
@Service
@Slf4j
public class SendMessageServiceProvider {
	private final ObjectProvider<SendMessageServiceImpl> serviceProvider;

	private ConcurrentMap<String, SendMessageServiceImpl> sendMessageServices = new ConcurrentHashMap<>();

	private ApplicationProperties properties;

	public SendMessageServiceProvider(ApplicationProperties properties,
			ObjectProvider<SendMessageServiceImpl> serviceProvider) {
		this.properties = properties;
		this.serviceProvider = serviceProvider;
	}

	/**
	 * メッセージ送信用クラスを取得する
	 * @param session
	 * @return
	 */
	public SendMessageService generateSendMessageService(WebSocketSession session) {
		return sendMessageServices.computeIfAbsent(session.getId(), id -> {
			// SpringのコンテキストからBeanを取得（この時DIも行われる）
			return serviceProvider.getObject(properties, session);
		});
	}

	/**
	 * チャンネルIDからサービスを取得する<br>
	 * 一致するサービスが無かった場合はnullを返す
	 * @param channelId
	 * @return
	 */
	public SendMessageService getServiceByChannelId(String channelId) {
		for (Entry<String, SendMessageServiceImpl> service : sendMessageServices.entrySet()) {
			if (service.getValue().getChannelId().equals(channelId)) {
				return service.getValue();
			}
		}
		return null;
	}
	
	/**
	 * ssrcからサービスを取得する<br>
	 * 一致するサービスが無かった場合はnullを返す
	 * @param channelId
	 * @return
	 */
	public SendMessageService getServiceBySsrc(int ssrc) {
		for (Entry<String, SendMessageServiceImpl> service : sendMessageServices.entrySet()) {
			if (service.getValue().getSsrc() == ssrc) {
				return service.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 特定のsessionに対して、チャンネルIDを設定する
	 * @param session
	 * @param channelId
	 * @return
	 */
	public boolean setChannelId(WebSocketSession session, String channelId) {
		SendMessageServiceImpl service = sendMessageServices.get(session.getId());
		if(service == null) {
			return false;
		}
		
		service.setChannelId(channelId);
		return true;
	}
	
	/**
	 * 特定のsessionに対して、チャンネルIDを設定する
	 * @param session
	 * @param channelId
	 * @return
	 */
	public boolean setSsrc(WebSocketSession session, int ssrc) {
		SendMessageServiceImpl service = sendMessageServices.get(session.getId());
		if(service == null) {
			return false;
		}
		
		service.setSsrc(ssrc);
		return true;
	}

	/**
	 * メッセージ送信用クラスを削除する
	 * @param session
	 */
	public void removeService(WebSocketSession session) {
		sendMessageServices.remove(session.getId());
		log.debug("ハートビートサービスを削除しました。現在有効なサービスは" + sendMessageServices.size() + "個です");
	}
}
