package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code8.Code8Dto;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatService;
import tokyo.archangel.sdb.discord.servicies.heartbeat.HeartBeatServiceProvider;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;

@Service
@Slf4j
public class VoiceOpcode8Service implements VoiceOpcodeServiceInterface {

	private SendMessageServiceProvider messageServiceProvider;

	private SendMessageService sendMessageService;

	private HeartBeatServiceProvider heartBeatServiceProvider;

	private VoiceChannels voiceChannels;

	public VoiceOpcode8Service(HeartBeatServiceProvider heartBeatServiceProvider, VoiceChannels voiceChannels,
			SendMessageServiceProvider messageServiceProvider) {
		this.heartBeatServiceProvider = heartBeatServiceProvider;
		this.voiceChannels = voiceChannels;
		this.messageServiceProvider = messageServiceProvider;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのopcode8を受信しました");
		Code8Dto code8dto;
		if (dto instanceof Code8Dto) {
			code8dto = (Code8Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}
		
		VoiceChannelInfo voiceInfo = voiceChannels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());
		voiceInfo.setHeartBeatInterval(code8dto.getDetail().getHeartbeatInterval());
		messageServiceProvider.setChannelId(sendMessageService.getSession(), voiceInfo.getChannelId());

		HeartBeatService heartBeatService = heartBeatServiceProvider
				.getHeartBeatService(sendMessageService.getSession());
		heartBeatService.setSendMessageService(sendMessageService);
		heartBeatService.setVoiceChannelInfo(voiceInfo);
		heartBeatService.exec(voiceInfo.getHeartBeatInterval());

		// TODO voiceハートビートの再接続処理
		// TODO ユーザー側から切断された時の処理
		// TODO Udpコネクションの掃除
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

}
