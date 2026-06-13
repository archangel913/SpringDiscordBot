package tokyo.archangel.sdb.internal.component.voice;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;

public class VoiceChannels {
	private final ObjectProvider<VoiceChannelInfo> infomation;

	private ConcurrentMap<String, VoiceChannelInfo> voiceChannelInfo = new ConcurrentHashMap<>();

	public VoiceChannels(ObjectProvider<VoiceChannelInfo> infomation) {
		this.infomation = infomation;
	}

	/**
	 * ボイスチャンネルの情報を取得します<br>
	 * すでに生成済みだった場合、それを返します。
	 * @param sessionId
	 * @return
	 */
	public VoiceChannelInfo generateInfo(String channelId) {
		return voiceChannelInfo.computeIfAbsent(channelId, id -> {
			return infomation.getObject();
		});
	}

	/**
	 * ボイスチャンネルの情報を削除します
	 * @param channelId
	 */
	public void removeInfoByChannelId(String channelId) {
		voiceChannelInfo.remove(channelId);
	}

	/**
	 * ボイスチャンネルの情報をギルドIDをもとに取得します
	 * @param sessionId
	 * @return
	 */
	public VoiceChannelInfo getInfoByGuildId(String guildId) {
		for (Entry<String, VoiceChannelInfo> info : voiceChannelInfo.entrySet()) {
			String voiceInfo = info.getValue().getGuildId();
			if (voiceInfo != null && voiceInfo.equals(guildId)) {
				return info.getValue();
			}
		}
		return null;
	}

	/**
	 * ボイスチャンネルの情報をssrcをもとに取得します
	 * @param sessionId
	 * @return
	 */
	public VoiceChannelInfo getInfoBySsrc(int ssrc) {
		for (Entry<String, VoiceChannelInfo> info : voiceChannelInfo.entrySet()) {
			int infoSsrc = info.getValue().getSsrc();
			if (infoSsrc == ssrc) {
				return info.getValue();
			}
		}
		return null;
	}

	/**
	 * ボイスチャンネルの情報をwebsocketのguidをもとに取得します
	 * @param sessionId
	 * @return
	 */
	public VoiceChannelInfo getInfoByWebsocketGuid(String guid) {
		for (Entry<String, VoiceChannelInfo> info : voiceChannelInfo.entrySet()) {
			String websocketGuid = info.getValue().getWebsocketGuid();
			if (websocketGuid != null && websocketGuid.equals(guid)) {
				return info.getValue();
			}
		}
		return null;
	}
}
