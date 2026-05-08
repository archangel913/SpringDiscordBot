package tokyo.archangel.sdb.discord.dto.gateway.opcode.code0.voicestateupdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Nameplate {
	@JsonProperty("sku_id")
	private String skuId;
	
	@JsonProperty("asset")
	private String asset;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("palette")
	private String palette;
}
