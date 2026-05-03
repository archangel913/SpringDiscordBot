package tokyo.archangel.sdb.discord.servicies.sendMessage;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.ApplicationProperties;

/**
 * メッセージ送信用サービスを提供するクラス
 */
@Service
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
	public SendMessageService generateSendMessageService(WebSocketSession session, long channelId) {
		return sendMessageServices.computeIfAbsent(session.getId(), id -> {
			// SpringのコンテキストからBeanを取得（この時DIも行われる）
			return serviceProvider.getObject(properties, session, channelId);
		});
	}

	/**
	 * チャンネルIDからサービスを取得する<br>
	 * 一致するサービスが無かった場合はnullを返す
	 * @param channelId
	 * @return
	 */
	public SendMessageService getServiceByChannelId(long channelId) {
		for (Entry<String, SendMessageServiceImpl> service : sendMessageServices.entrySet()) {
			if (service.getValue().getChannelId() == channelId) {
				return service.getValue();
			}
		}
		return null;
	}

	// TODO メモリリーク対策（マップの中身を削除する）
}
