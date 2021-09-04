package akurczych.serialisation.event;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@ToString(callSuper=true)
public class ReinstateCustomer extends CustomerPayload {
	static final String TYPE = "REINSTATE_CUSTOMER";
	
	public ReinstateCustomer(@JsonProperty("id") final UUID id) {
		super(id);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

}
