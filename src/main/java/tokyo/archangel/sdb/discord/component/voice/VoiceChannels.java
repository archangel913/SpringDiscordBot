package tokyo.archangel.sdb.discord.component.voice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class VoiceChannels {
	private final ObjectProvider<VoiceChannelInfo> infomation;

	private ConcurrentMap<String, VoiceChannelInfo> voiceChannelInfo = new ConcurrentHashMap<>();

	public VoiceChannels(ObjectProvider<VoiceChannelInfo> infomation) {
		this.infomation = infomation;
	}
	
	/**
	 * ボイスチャンネルの情報を取得します
	 * @param guildId
	 * @return
	 */
	public VoiceChannelInfo getVoiceChannelInfo(String sessionId) {
		return voiceChannelInfo.computeIfAbsent(sessionId, id -> {
			return infomation.getObject();
		});
	}
	
	/**
	 * ボイスチャンネルの情報を削除します
	 * @param channelId
	 */
	public void removeVoiceChannelInfo(String sessionId) {
		voiceChannelInfo.remove(sessionId);
	}
}
