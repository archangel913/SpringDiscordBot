package tokyo.archangel.sdb.discord.component.voice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class VoiceConnectionInfo {
	private List<Stream> streams = Collections.synchronizedList(new ArrayList<>());

	private Integer targetPort;

	private List<String> modes = Collections.synchronizedList(new ArrayList<>());

	private String targetIp;

	private Integer heartbeatInterval;

	private List<String> experiments  = Collections.synchronizedList(new ArrayList<>());

	private String videoCodec;

	private Integer secureFramesVersion;

	private byte[] secretKey;

	private String mediaSessionId;

	private Integer daveProtocolVersion;

	private String audioCodec;
}
