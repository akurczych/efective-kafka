package akurczych.kafkagettingstarted;

import static java.lang.System.out;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public final class BasicConsumer {
	public static void main(String[] args) {

		final var topic = "getting-started";

		final Map<String, Object> config = Map.of(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
				ConsumerConfig.GROUP_ID_CONFIG, "basic-consumer",
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
				ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

		try (var consumer = new KafkaConsumer<String, String>(config)) {
			consumer.subscribe(Set.of(topic));
			
			while (true) {
				final var records = consumer.poll(Duration.ofMillis(100));
				for(var record : records) {
					out.format("Got record with value %s%n", record.value());
				}
				consumer.commitAsync();
			}
		}
	}
}
