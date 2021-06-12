package akurczych.kafkagettingstarted;

import static java.lang.System.out;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public final class BasicProducerSample {
	public static void main(String[] args) throws InterruptedException {

		final var topic = "getting-started";

		final Map<String, Object> config = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
				ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

		try (var producer = new KafkaProducer<String, String>(config)) {
			while (true) {
				final var key = "myKey";
				final var value = LocalDateTime.now().toString();
				out.format("Publishing record with value %s%n", value);
				final Callback callback = (metadata, exception) -> {
					out.format("Published with metadata: %s, error: s%n%", metadata, exception);
				};

				producer.send(new ProducerRecord<>(topic, key, value), callback);
				Thread.sleep(1000);
			}
		}
	}
}
