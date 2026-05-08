package tokyo.archangel.sdb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;

@RestController
public class Test {
	private SendMessageServiceProvider sendMessageServiceProvider;
	public Test(SendMessageServiceProvider sendMessageServiceProvider) {
		this.sendMessageServiceProvider = sendMessageServiceProvider;
	}

	@PostMapping("/test")
	public void test(@RequestBody String body) {
		sendMessageServiceProvider.getServiceByChannelId(-1).sendMessage(body);
	}
}
