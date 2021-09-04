package akurczych.serialisation.event;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type")
@JsonSubTypes({
	@JsonSubTypes.Type(value=CreateCustomer.class, name=CreateCustomer.TYPE),
	@JsonSubTypes.Type(value=UpdateCustomer.class, name=UpdateCustomer.TYPE),
	@JsonSubTypes.Type(value=SuspendCustomer.class, name=SuspendCustomer.TYPE),
	@JsonSubTypes.Type(value=ReinstateCustomer.class, name=ReinstateCustomer.TYPE)
})
@RequiredArgsConstructor
@ToString
public abstract class CustomerPayload {
	@JsonProperty
	@Getter
	private final UUID id;
	
	public abstract String getType();
	
}
