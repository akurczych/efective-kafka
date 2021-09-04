package akurczych.serialisation.business;

import java.util.UUID;

import akurczych.serialisation.event.CreateCustomer;
import akurczych.serialisation.event.CustomerPayload;
import akurczych.serialisation.event.ReinstateCustomer;
import akurczych.serialisation.event.SuspendCustomer;
import akurczych.serialisation.event.UpdateCustomer;
import akurczych.serialisation.sender.EventSender;
import akurczych.serialisation.sender.EventSender.SendException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProducerBusinessLogic {

	private final EventSender sender;
	
	public void generateRandomEvents() throws InterruptedException, SendException {
		final var create = new CreateCustomer(UUID.randomUUID(), "Bob", "Brown");
		blockingSend(create);
		
		if (Math.random() > 0.5) {
			final var update = new UpdateCustomer(create.getId(), "Charlie", "Brown");
			blockingSend(update);
		}
		
		if(Math.random() > 0.5) {
			final var suspend = new SuspendCustomer(create.getId());
			blockingSend(suspend);
			
			if(Math.random() > 0.5) {
				final var reinstate = new ReinstateCustomer(create.getId());
				blockingSend(reinstate);
			}
		}
	}
	
	private void blockingSend(CustomerPayload payload) throws InterruptedException, SendException {
		System.out.format("Publising %s%n", payload);
		sender.blockingSend(payload);
	}
}
