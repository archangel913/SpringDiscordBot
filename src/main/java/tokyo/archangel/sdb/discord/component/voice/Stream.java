package tokyo.archangel.sdb.discord.component.voice;

import lombok.Data;

@Data
public class Stream {
	private String type;

	private Integer ssrc;

	private Integer rtxSsrc;

	private String rid;

	private Integer quality;

	private Boolean active;
}
