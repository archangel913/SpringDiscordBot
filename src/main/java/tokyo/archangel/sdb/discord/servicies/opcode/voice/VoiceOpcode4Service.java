package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.component.voice.VoiceConnectionInfo;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.discord.servicies.libdave.DaveServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class VoiceOpcode4Service implements VoiceOpcodeServiceInterface {

	private SendMessageService sendMessageService;

	private VoiceChannels channels;

	private DaveServiceProvider daveServiceProvider;

	public VoiceOpcode4Service(VoiceChannels channels, DaveServiceProvider daveServiceProvider) {
		this.channels = channels;
		this.daveServiceProvider = daveServiceProvider;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのopcode4を受信しました");
		Code4Dto code4dto;
		if (dto instanceof Code4Dto) {
			code4dto = (Code4Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		VoiceChannelInfo voiceInfo = channels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());

		setInfo(voiceInfo, code4dto.getDetail());

		// davaの初期化
		daveServiceProvider.generateDaveService(sendMessageService.getSession().getId(), voiceInfo.getChannelId(),
				voiceInfo.getUserId(), voiceInfo.getSsrc(), voiceInfo);
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

	private void setInfo(VoiceChannelInfo voiceInfo, Code4Detail detail) {
		VoiceConnectionInfo info = voiceInfo.getInfo();
		info.setVideoCodec(detail.getVideoCodec());
		info.setSecureFramesVersion(detail.getSecureFramesVersion());
		info.setMediaSessionId(detail.getMediaSessionId());
		info.setDaveProtocolVersion(detail.getDaveProtocolVersion());
		info.setAudioCodec(detail.getAudioCodec());
		info.setSecretKey(detail.getSecretKey());

		List<String> modes = info.getModes();
		modes.clear();
		modes.add(detail.getMode());
	}
}
