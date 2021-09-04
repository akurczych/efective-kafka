package akurczych.serialisation.receiver;

@FunctionalInterface
public interface EventListener {
void onEvent(ReceiveEvent event);
}
