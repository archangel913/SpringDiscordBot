package tokyo.archangel.sdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringDiscordBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDiscordBotApplication.class, args);
	}

}
