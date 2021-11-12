package akurczych.typesafeproducer;

import java.util.Date;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class TypeSafeProducerSample {
    
    public static void main (String[] args) throws InterruptedException  {
        final var config = new TypeSafeProducerConfig()
                .withBootstrapServer("localhost:9092")
                .withKeySerializerClass(StringSerializer.class)
                .withValueSerializerClass(StringSerializer.class)
                .withCustomEntry(ProducerConfig.CLIENT_ID_CONFIG, "typesafe-sample")
                .withCustomEntry(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        
        try (Producer<String, String> producer = new KafkaProducer<>(config.mapify())) {
            while (true) {
                final String value = new Date().toString();
                System.out.format("Publishing record with value %s%n", value);
                producer.send(new ProducerRecord<>("test", "myKey", value));
                Thread.sleep(1000);
            }
        }
    }
    
}
