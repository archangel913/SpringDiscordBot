package tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicechannelstatusupdate.VoiceChannelStatusUpdateDetail;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.OpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class VoiceChannelStatusUpdateService implements OpcodeServiceInterface {
	private VoiceChannels channels;

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

		if (detail.getStatus() == null) {
			channels.removeVoiceChannelInfo(detail.getId());
		} else {
			channels.setVoiceChannelInfo(detail);
		}
		
		VoiceChannelInfo info = channels.getVoiceChannelInfo(detail.getId());
		if(info == null) {
			log.trace("表示できる情報がありません");
		} else {
			log.trace(info.toString());
		}
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		// 使用しないので空実装
	}

}
