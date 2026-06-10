package tokyo.archangel.sdb.discord.servicies.opcode.gateway.dispatch;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.gateway.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.Code0Dto;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicestateupdate.VoiceStateUpdateDetail;
import tokyo.archangel.sdb.discord.servicies.opcode.gateway.GatewayOpcodeServiceInterface;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Slf4j
public class VoiceStateUpdateService implements GatewayOpcodeServiceInterface {
	private VoiceChannels channels;
	
	private SendMessageService sendMessageService;

	public VoiceStateUpdateService(VoiceChannels channels) {
		this.channels = channels;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("VoiceStateUpdateイベントを受け取りました");
		VoiceStateUpdateDetail detail;
		if (dto instanceof Code0Dto && ((Code0Dto) dto).getDetail() instanceof VoiceStateUpdateDetail) {
			detail = (VoiceStateUpdateDetail) ((Code0Dto) dto).getDetail();
		} else {
			log.warn("想定外の型のため処理を実行しません");
			return;
		}
		
		if(detail.getChannelId() == null) {
			return;
		}
		
		VoiceChannelInfo info = channels.generateInfo(detail.getChannelId());
		info.setChannelId(detail.getChannelId());
		info.setGuildId(detail.getGuildId());
		info.setSessionId(detail.getSessionId());
		info.setUserId(detail.getUserId());
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}
}
