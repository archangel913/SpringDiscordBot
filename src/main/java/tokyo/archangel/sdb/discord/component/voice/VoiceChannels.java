package tokyo.archangel.sdb.discord.component.voice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicechannelstarttimeupdate.VoiceChannelStartTimeUpdateDetail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicechannelstatusupdate.VoiceChannelStatusUpdateDetail;

@Component
public class VoiceChannels {
	private final ObjectProvider<VoiceChannelInfo> infomation;

	private ConcurrentMap<String, VoiceChannelInfo> voiceChannelInfo = new ConcurrentHashMap<>();

	public VoiceChannels(ObjectProvider<VoiceChannelInfo> infomation) {
		this.infomation = infomation;
	}

	/**
	 * ボイスチャンネルの情報を取得します
	 * @param channelId
	 * @return
	 */
	public VoiceChannelInfo getVoiceChannelInfo(String channelId) {
		return voiceChannelInfo.get(channelId);
	}

	/**
	 * ボイスチャンネルの情報を設定します
	 * @param detail
	 */
	public void setVoiceChannelInfo(VoiceChannelStartTimeUpdateDetail detail) {
		VoiceChannelInfo info = generateVoiceChannelInfo(detail.getId());
		info.setChannelId(detail.getId());
		info.setGuildId(detail.getGuildId());
		info.setStartTime(LocalDateTime.ofEpochSecond(detail.getVoiceStartTime(), 0, ZoneOffset.ofHours(9)));
	}

	/**
	 * ボイスチャンネルの情報を設定します
	 * @param detail
	 */
	public void setVoiceChannelInfo(VoiceChannelStatusUpdateDetail detail) {
		VoiceChannelInfo info = generateVoiceChannelInfo(detail.getId());
		info.setChannelId(detail.getId());
		info.setGuildId(detail.getGuildId());
		info.setStatus(detail.getStatus());
	}
	
	/**
	 * ボイスチャンネルの情報を削除します
	 * @param channelId
	 */
	public void removeVoiceChannelInfo(String channelId) {
		voiceChannelInfo.remove(channelId);
	}

	private VoiceChannelInfo generateVoiceChannelInfo(String channelId) {
		return voiceChannelInfo.computeIfAbsent(channelId, id -> {
			return infomation.getObject();
		});
	}
}
