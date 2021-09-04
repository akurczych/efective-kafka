package akurczych.serialisation.business;

import akurczych.serialisation.receiver.EventListener;
import akurczych.serialisation.receiver.EventReceiver;
import akurczych.serialisation.receiver.ReceiveEvent;

public final class ConsumerBusinessLogic implements EventListener{
	private final EventReceiver receiver;
	
	public ConsumerBusinessLogic(EventReceiver receiver) {
		this.receiver = receiver;
		receiver.addListener(this::onEvent);
	}

	public void onEvent(ReceiveEvent event) {
		if(!event.isError()) {
			System.out.format("Received %s%n", event.getPayload());
		} else {
			System.err.format("Error in record %s: %s%n", event.getRecord(), event.getError());
		}
	}
}
