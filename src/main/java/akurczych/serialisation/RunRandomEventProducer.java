package akurczych.serialisation;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;

import akurczych.serialisation.business.ProducerBusinessLogic;
import akurczych.serialisation.sender.DirectSender;
import akurczych.serialisation.sender.EventSender.SendException;

public class RunRandomEventProducer {
	public static void main(String[] args) throws InterruptedException, SendException {
		
		final var config = Map.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
				ProducerConfig.CLIENT_ID_CONFIG, "customer-producer-sample");

		final var topic = "customer.test";

		try (var sender = new DirectSender(config, topic)) {
			final var businessLogic = new ProducerBusinessLogic(sender);
			while (true) {
				businessLogic.generateRandomEvents();
				Thread.sleep(500);
			}
		}
	}
}
