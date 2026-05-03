package tokyo.archangel.sdb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Detail;
import tokyo.archangel.sdb.discord.dto.gateway.opcode.code4.Code4Dto;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tools.jackson.databind.ObjectMapper;

@RestController
public class Test {
	private SendMessageServiceProvider sendMessageServiceProvider;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public Test(SendMessageServiceProvider sendMessageServiceProvider) {
		this.sendMessageServiceProvider = sendMessageServiceProvider;
	}

	@PostMapping("/test")
	public void test() {
		Code4Detail detail = new Code4Detail("1019297738640330803", "1140468827893809243", false, false);

		Code4Dto dto = new Code4Dto(detail);
		
		String json = objectMapper.writeValueAsString(dto);
		
		sendMessageServiceProvider.getServiceByChannelId(-1).sendMessage(json);
	}
}
