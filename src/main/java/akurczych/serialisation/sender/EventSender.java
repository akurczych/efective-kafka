package akurczych.serialisation.sender;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;

import akurczych.serialisation.event.CustomerPayload;

public interface EventSender extends Closeable {

	Future<RecordMetadata> send(CustomerPayload payload);
	
	default RecordMetadata blockingSend(CustomerPayload payload) throws InterruptedException, SendException {
		try {
			return send(payload).get();
		} catch (ExecutionException e) {
			throw new SendException(e.getCause());
		}
	}
	
	@Override
	public void close();
	
	@SuppressWarnings("serial")
	final class SendException extends Exception {
		SendException(Throwable cause) {
			super(cause);
		}
	}
	
}
