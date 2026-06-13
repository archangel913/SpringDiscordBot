package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.voicestateupdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class AvatarDecorationData {
	@JsonProperty("asset")
	private String asset;
	
	@JsonProperty("sku_id")
	private String sku_id;
}
