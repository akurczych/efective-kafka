package akurczych.serialisation.receiver;

import akurczych.serialisation.event.CustomerPayload;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CustomerPayloadOrError {
	private final CustomerPayload payload;
	private final Throwable error;
	private String encodedValue;
	
	public CustomerPayloadOrError(CustomerPayload payload, Throwable error, String encodedValue) {
		this.payload = payload;
		this.error = error;
		this.encodedValue = encodedValue;
	}
	
	
}
