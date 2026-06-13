package tokyo.archangel.sdb.internal.dto.voice.opcode.code2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Streams {
	@JsonProperty("type")
	private String type;

	@JsonProperty("ssrc")
	private Integer ssrc;

	@JsonProperty("rtx_ssrc")
	private Integer rtxSsrc;

	@JsonProperty("rid")
	private String rid;

	@JsonProperty("quality")
	private Integer quality;

	@JsonProperty("active")
	private Boolean active;
}
