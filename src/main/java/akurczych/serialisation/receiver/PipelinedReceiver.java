package akurczych.serialisation.receiver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.obsidiandynamics.worker.Terminator;
import com.obsidiandynamics.worker.WorkerOptions;
import com.obsidiandynamics.worker.WorkerThread;

public final class PipelinedReceiver extends AbstractReceiver {
    private final WorkerThread pollingThread;
    private final WorkerThread processingThread;
    private final Consumer<String, CustomerPayloadOrError> consumer;
    private final Duration pollTimeout;
    private final BlockingQueue<ReceiveEvent> receivedEvents;
    private final Queue<Map<TopicPartition, OffsetAndMetadata>> pendingOffsets = new LinkedBlockingQueue<>();
    
    public PipelinedReceiver(Map<String, Object> consumerConfig,
                             String topic,
                             Duration pollTimeout,
                             int queueCapacity) {
        this.pollTimeout = pollTimeout;
        this.receivedEvents = new LinkedBlockingQueue<>(queueCapacity);
        final var mergedConfig = new HashMap<String, Object>();
        mergedConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        mergedConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomerPayloadDeserializer.class.getName());
        mergedConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        mergedConfig.putAll(consumerConfig);
        this.consumer = new KafkaConsumer<>(mergedConfig);
        this.consumer.subscribe(Set.of(topic));
        this.pollingThread = WorkerThread.builder()
                                         .withOptions(new WorkerOptions().daemon()
                                                                         .withName(PipelinedReceiver.class, "poller"))
                                         .onCycle(this::onPollCycle)
                                         .build();
        this.processingThread = WorkerThread.builder()
                                            .withOptions(new WorkerOptions().daemon()
                                                                            .withName(PipelinedReceiver.class, "processor"))
                                            .onCycle(this::onProcessCycle)
                                            .build();
    }
    

    @Override
    public void start() {
        pollingThread.start();
        processingThread.start();
    }

    @Override
    public void close() {
        Terminator.of(pollingThread, processingThread)
                  .terminate()
                  .joinSilently();
        consumer.close();
    }

    private void onPollCycle(WorkerThread t) throws InterruptedException {
        final ConsumerRecords<String, CustomerPayloadOrError> records;
        
        try {
            records = consumer.poll(pollTimeout);
        } catch(InterruptException e) {
            throw new InterruptedException("Interrupted during poll");
        }
        
        if(!records.isEmpty()) {
            for(var record : records) {
                final var value = record.value();
                final var event = new ReceiveEvent(value.getPayload(),
                                                   value.getError(),
                                                   record,
                                                   value.getEncodedValue());
                receivedEvents.put(event);
            }
        }
        
        for(Map<TopicPartition, OffsetAndMetadata> pendingOffset; (pendingOffset = pendingOffsets.poll()) != null;) {
            consumer.commitAsync(pendingOffset, null);
        }
    }

    private void onProcessCycle(WorkerThread t) throws InterruptedException {
        final var event = receivedEvents.take();
        fire(event);
        final var record = event.getRecord();
        pendingOffsets.add(Map.of(new TopicPartition(record.topic(), record.partition()),
                                  new OffsetAndMetadata(record.offset() + 1)));
    }

}
