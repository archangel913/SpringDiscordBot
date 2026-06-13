package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voicestateupdate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Collectibles {
	@JsonProperty("nameplate")
	private List<Nameplate> nameplate;
}
