package tokyo.archangel.sdb.discord.servicies.opcode.voice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.component.voice.Stream;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannelInfo;
import tokyo.archangel.sdb.discord.component.voice.VoiceChannels;
import tokyo.archangel.sdb.discord.component.voice.VoiceConnectionInfo;
import tokyo.archangel.sdb.discord.dto.voice.OpCodeReceiveBaseDto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code2.Code2Dto;
import tokyo.archangel.sdb.discord.dto.voice.opcode.code2.Streams;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageService;
import tokyo.archangel.sdb.discord.servicies.sendMessage.SendMessageServiceProvider;
import tokyo.archangel.sdb.discord.servicies.voice.VoiceSessionProvider;
import tokyo.archangel.sdb.discord.udp.UdpConnection;

@Service
@Slf4j
public class VoiceOpcode2Service implements VoiceOpcodeServiceInterface {

	private SendMessageServiceProvider messageServiceProvider;

	private SendMessageService sendMessageService;

	private VoiceSessionProvider voiceSessionProvider;

	private VoiceChannels channels;

	public VoiceOpcode2Service(VoiceSessionProvider voiceSessionProvider,
			SendMessageServiceProvider messageServiceProvider,
			VoiceChannels channels) {
		this.voiceSessionProvider = voiceSessionProvider;
		this.messageServiceProvider = messageServiceProvider;
		this.channels = channels;
	}

	@Override
	public void exec(OpCodeReceiveBaseDto dto) {
		log.debug("voiceのopcode2を受信しました");
		Code2Dto code2dto;
		if (dto instanceof Code2Dto) {
			code2dto = (Code2Dto) dto;
		} else {
			log.warn("必要なデータが揃っていないため処理を実行しません");
			return;
		}

		// メッセージサービスにssrcを設定
		messageServiceProvider.setSsrc(sendMessageService.getSession(), code2dto.getDetail().getSsrc());

		VoiceChannelInfo voiceInfo = channels.getInfoByWebsocketGuid(sendMessageService.getSession().getId());

		// 各種値を設定
		setInfomation(code2dto, voiceInfo);

		// UDPコネクションを作成
		String ip = code2dto.getDetail().getIp();
		int port = code2dto.getDetail().getPort();
		try {
			UdpConnection udpConnection = voiceSessionProvider.getUdpConnection(voiceInfo.getChannelId(), ip, port);
			byte[] data = generateIpDiscoveryPayload(code2dto);
			udpConnection.send(data);
		} catch (IOException e) {
			log.error("IpDiscovery送信に失敗しました。", e);
		}
	}

	@Override
	public void setSendSessageService(SendMessageService sendMessageService) {
		this.sendMessageService = sendMessageService;
	}

	private byte[] generateIpDiscoveryPayload(Code2Dto code2dto) {
		int type = 1;
		int length = 70;
		int ssrc = code2dto.getDetail().getSsrc();
		byte[] address = code2dto.getDetail().getIp().getBytes(StandardCharsets.UTF_8);
		int port = code2dto.getDetail().getPort();

		ByteBuffer addressBuffer = ByteBuffer.allocate(64);
		addressBuffer.put(address, 0, Math.min(address.length, 64));

		ByteBuffer buffer = ByteBuffer.allocate(74);

		// エンディアンの設定 (ネットワーク通信は基本的にBIG_ENDIAN)
		buffer.order(ByteOrder.BIG_ENDIAN);

		// データの書き込み
		buffer.putShort((short) type);
		buffer.putShort((short) length);
		buffer.putInt(ssrc);
		buffer.put(addressBuffer);
		buffer.putShort((short) port);

		return buffer.array();
	}

	private void setInfomation(Code2Dto code2dto, VoiceChannelInfo voiceInfo) {
		VoiceConnectionInfo connectionInfo = new VoiceConnectionInfo();

		List<Stream> streams = new ArrayList<Stream>();
		for (Streams oldStream : code2dto.getDetail().getStreams()) {
			streams.add(getStream(oldStream));
		}
		connectionInfo.getStreams().addAll(streams);
		connectionInfo.setTargetIp(code2dto.getDetail().getIp());
		connectionInfo.setTargetPort(code2dto.getDetail().getPort());
		connectionInfo.getModes().addAll(code2dto.getDetail().getModes());
		connectionInfo.setHeartbeatInterval(code2dto.getDetail().getHeartbeatInterval());
		connectionInfo.getExperiments().addAll(code2dto.getDetail().getExperiments());
		voiceInfo.setInfo(connectionInfo);
		voiceInfo.setSsrc(code2dto.getDetail().getSsrc());
	}

	private Stream getStream(Streams old) {
		Stream stream = new Stream();
		stream.setType(old.getType());
		stream.setActive(old.getActive());
		stream.setQuality(old.getQuality());
		stream.setRid(old.getRid());
		stream.setRtxSsrc(old.getRtxSsrc());
		stream.setSsrc(old.getSsrc());
		return stream;
	}
}
