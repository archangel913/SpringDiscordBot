package tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.resumed;

import java.beans.Introspector;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.EventDetailBase;
import tokyo.archangel.sdb.internal.servicies.opcode.gateway.dispatch.ResumedService;

@Value
@EqualsAndHashCode(callSuper = true)
public class ResumedDetail extends EventDetailBase {

	@JsonProperty("_trace")
	private List<String> voiceStartTime;

	@JsonIgnore
	@Override
	public String getServiceClassName() {
		String className = ResumedService.class.getSimpleName();
		return Introspector.decapitalize(className);
	}
}
