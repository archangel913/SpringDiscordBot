package tokyo.archangel.sdb.internal.servicies.opcode.voice;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.internal.component.voice.VoiceChannels;
import tokyo.archangel.sdb.internal.component.voice.VoiceConnectionInfo;
import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.internal.dto.voice.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.internal.servicies.voice.VoiceResourceProvider;

@Slf4j
public class VoiceOpcode4Service implements VoiceOpcodeServiceInterface {

	private SendMessageService sendMessageService;

	private VoiceChannels channels;

	private VoiceResourceProvider voiceSessionProvider;

	public VoiceOpcode4Service(VoiceChannels channels, VoiceResourceProvider voiceSessionProvider) {
		this.channels = channels;
		this.voiceSessionProvider = voiceSessionProvider;
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
		try {
			voiceSessionProvider.getE2eeCryptService(sendMessageService.getSession().getId(), voiceInfo.getChannelId(),
					voiceInfo.getUserId(), voiceInfo.getSsrc(), voiceInfo);
		} catch (Exception e) {
			log.error("E2EE暗号化サービスの初期化に失敗しました。", e);
		}
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
