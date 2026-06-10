package tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicechannelstatusupdate.VoiceChannelStatusUpdateDetail;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Slf4j
public class VoiceChannelStatusUpdateService implements GatewayOpcodeServiceInterface {
	private VoiceChannels channels;

	private SendMessageService sendMessageService;

	public VoiceChannelStatusUpdateService(VoiceChannels channels) {
		this.channels = channels;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("VoiceChannelStartTimeUpdateイベントを受け取りました");
		VoiceChannelStatusUpdateDetail detail;
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof VoiceChannelStatusUpdateDetail) {
			detail = (VoiceChannelStatusUpdateDetail) ((Code0Dto) dto).getDetail();
		} else {
			log.warn("想定外の型のため処理を実行しません");
			return;
		}

		VoiceChannelInfo info = channels.generateInfo(detail.getId());
		if (detail.getStatus() == null) {
			channels.removeInfoByChannelId(detail.getGuildId());
		} else {
			info.setChannelId(detail.getId());
			info.setGuildId(detail.getGuildId());
			info.setStatus(detail.getStatus());
		}

		if (info == null) {
			log.trace("表示できる情報がありません");
		} else {
			log.trace(info.toString());
		}
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

}
