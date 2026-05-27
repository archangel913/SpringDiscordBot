package tokyo.archangel.sdb.discord.dto.voice.opcode.code11;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Code11Detail {
	@JsonProperty("user_ids")
	private List<String> userIds;
}
