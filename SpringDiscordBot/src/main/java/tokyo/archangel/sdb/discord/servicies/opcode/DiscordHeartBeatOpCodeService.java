package tokyo.archangel.sdb.discord.servicies.opcode;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.WebSocketSession;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

// 命名きもい
@Slf4j
public class DiscordHeartBeatOpCodeService extends AbstractOpCodeService{
	private int interval;
	
	@Setter
	private String sequence = null;
	
	public DiscordHeartBeatOpCodeService(WebSocketSession session, int interval) {
		super(session);
		this.interval = interval;
	}
	
	@Async
	@Override
	public void exec() {
		while(true) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			log.debug("バックグラウンドのハートビートを送信します");
			new HeartBeatOpCodeService(session, sequence).exec();
		}
		
	}
}
