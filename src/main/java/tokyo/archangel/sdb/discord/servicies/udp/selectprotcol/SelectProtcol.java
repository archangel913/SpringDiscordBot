package tokyo.archangel.sdb.discord.servicies.udp.selectprotcol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code1.Code1Detail;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code1.Code1Dto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code1.Data;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.servicies.udp.UdpServiceInterface;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class SelectProtcol implements UdpServiceInterface {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private VoiceChannels channels;

	private SendMessageServiceProvider messageProvider;

	public SelectProtcol(VoiceChannels channels, SendMessageServiceProvider messageProvider) {
		this.channels = channels;
		this.messageProvider = messageProvider;
	}

	@Override
	public void exec(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data, 0, data.length);
		buffer.order(ByteOrder.BIG_ENDIAN);

		buffer.position(4);
		int ssrc = buffer.getInt();

		VoiceChannelInfo voiceInfo = channels.getInfoBySsrc(ssrc);
		String mode = voiceInfo.getInfo().getModes().get(0);

		byte[] byteAddress = new byte[64];
		buffer.get(byteAddress);
		String address = new String(byteAddress, StandardCharsets.UTF_8);
		int nullPos = address.indexOf('\0');
		if (nullPos != -1) {
			address = address.substring(0, nullPos);
		}
		int port = Short.toUnsignedInt(buffer.getShort());

		Code1Dto dto = new Code1Dto(new Code1Detail("udp", new Data(address, port, mode)));
		String json = objectMapper.writeValueAsString(dto);
		
		SendMessageService messageService = messageProvider.getServiceBySsrc(ssrc);
		messageService.sendMessage(json);
	}
}
