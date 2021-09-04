package akurczych.serialisation.event;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper=true)
public class CreateCustomer extends CustomerPayload {
	static final String TYPE = "CREATE_CUSTOMER";
	
	@JsonProperty
	@Getter
	private final String firstName;
	
	@JsonProperty
	@Getter
	private final String lastName;
	
	public CreateCustomer(@JsonProperty("id") final UUID id,
						  @JsonProperty("firstName") final String firstName,
						  @JsonProperty("lastName") final String lastName) {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

}
