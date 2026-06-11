package tokyo.archangel.sdb.internal.servicies.opcode.voice;

import java.util.Map;

import tokyo.archangel.sdb.internal.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.internal.servicies.sendMessage.SendMessageService;

public class VoiceOpcodeServiceFactory {
	private Map<String, VoiceOpcodeServiceInterface> voiceOpCodeServices;

	public VoiceOpcodeServiceFactory(Map<String, VoiceOpcodeServiceInterface> voiceOpCodeServices) {
		this.voiceOpCodeServices = voiceOpCodeServices;
	}

	/**
	 * オペコードに応じたサービスクラスを生成します
	 * @param baseDto
	 * @param session
	 * @return
	 */
	public VoiceOpcodeServiceInterface create(OpCodeReceiveBaseDto baseDto, SendMessageService sendMessageService) {
		VoiceOpcodeServiceInterface service = voiceOpCodeServices.get(baseDto.getServiceClassName());
		if (service == null) {
			return null;
		}
		service.setSendSessageService(sendMessageService);
		return service;
	}
}
