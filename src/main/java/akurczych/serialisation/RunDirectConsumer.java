package akurczych.serialisation;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import akurczych.serialisation.business.ConsumerBusinessLogic;
import akurczych.serialisation.receiver.DirectReceiver;

public final class RunDirectConsumer {
	public static void main(String[] args) throws InterruptedException {
		final Map<String, Object> consumerConfig = Map.of(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localHost:9092",
				ConsumerConfig.GROUP_ID_CONFIG, "customer-direct-consumer",
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		try(var receiver = new DirectReceiver(consumerConfig,
											  "customer.test",
											  Duration.ofMillis(100))) {
			new ConsumerBusinessLogic(receiver);
			receiver.start();
			Thread.sleep(10_000);
		}
	}
}