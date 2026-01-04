package tokyo.archangel.sdb.discord.servicies.opcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import tokyo.archangel.sdb.discord.dto.opcode.OpCodeBaseDto;
import tokyo.archangel.sdb.discord.dto.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.discord.dto.opcode.code10.Code10Dto;

@Component
public class OpCodeServiceFactory {
	@Autowired
	private ApplicationContext context;

	public AbstractOpCodeService create(OpCodeBaseDto baseDto, WebSocketSession session) {
		switch (baseDto) {
		case Code1Dto dto:
			return context.getBean(OpCode1Service.class, dto, session);
		case Code10Dto dto:
			return context.getBean(OpCode10Service.class, dto, session);
		default:
			throw new IllegalArgumentException("Unknown type");
		}
	}
}
