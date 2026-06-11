package tokyo.archangel.sdb.internal.dto.voice.opcode.code4;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code4Detail {
	@JsonProperty("video_codec")
	private String videoCodec;
	
	@JsonProperty("secure_frames_version")
	private Integer secureFramesVersion;
	
	@JsonProperty("secret_key")
	private byte[] secretKey;
	
	@JsonProperty("mode")
	private String mode;
	
	@JsonProperty("media_session_id")
	private String mediaSessionId;
	
	@JsonProperty("dave_protocol_version")
	private Integer daveProtocolVersion;
	
	@JsonProperty("audio_codec")
	private String audioCodec;
}
