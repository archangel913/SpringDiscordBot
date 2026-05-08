package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;

@Service
@Slf4j
public class Opcode2Service implements OpcodeServiceInterface {

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
