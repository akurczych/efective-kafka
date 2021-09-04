package akurczych.serialisation.event;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@ToString(callSuper=true)
public class SuspendCustomer extends CustomerPayload {
	static final String TYPE = "SUSPEND_CUSTOMER";
	
	public SuspendCustomer(@JsonProperty("id") final UUID id) {
		super(id);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

}
