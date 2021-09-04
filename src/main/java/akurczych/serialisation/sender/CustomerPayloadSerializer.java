package akurczych.serialisation.sender;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import akurczych.serialisation.event.CustomerPayload;

public class CustomerPayloadSerializer implements Serializer<CustomerPayload> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("serial")
	private static final class MarshallingException extends RuntimeException {
		private MarshallingException(Throwable cause) {
			super(cause);
		}
	}

	@Override
	public byte[] serialize(String topic, CustomerPayload data) {
		try {
			return objectMapper.writeValueAsBytes(data);
		} catch (JsonProcessingException e) {
			throw new MarshallingException(e);
		}
	}

}
