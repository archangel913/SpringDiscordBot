package tokyo.archangel.sdb.discord.servicies.opcode;

import java.io.IOException;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.dto.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.discord.dto.opcode.code10.Code10Dto;

@Component
@Scope("prototype")
@Slf4j
public class OpCode10Service extends AbstractOpCodeService{
	private Code10Dto dto;
	
	public OpCode10Service(Code10Dto dto, WebSocketSession session) {
		super(session);
		this.dto = dto;
	}
	
	@Async
	@Override
	public void exec() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(dto.getDetail().getHeartbeatInterval());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			log.debug("バックグラウンドでハートビートを送信します");
			new OpCode1Service(new Code1Dto(null),session).exec();
		}
		
		// websocketの切断
		try {
			log.debug("websocketを切断します");
			session.close();
			log.info("discordから切断完了");
		} catch (IOException e) {
			log.error("discordの切断中にエラーが発生しました",e);
		}
	}
}
