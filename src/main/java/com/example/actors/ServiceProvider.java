package com.example.actors;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

import com.example.messages.Busy;
import com.example.messages.ProviderFail;
import com.example.messages.Recover;
import com.example.messages.Restart;
import com.example.messages.SimulatedErrorMessage;
import com.example.messages.SubscriptionApprovement;
import com.example.messages.SubscriptionRequest;
import com.example.messages.SuccessfulApprove;

public class ServiceProvider extends UntypedActor {
	final private int WORK_DELAY = 3;
	final private int MAX_COUNTER = 3;
	static private int nTasks = 0;
	final String APPROVE_MESSAGE = "approved";

	/*
	 * @Override public void preStart() throws Exception {
	 * System.out.println("Service provider was restarted"); super.preStart();
	 * };
	 */
	// Simulate provider is restarted, just send service restarting message
	// back.
	Procedure<Object> restart = new Procedure<Object>() {
		@Override
		public void apply(Object message) throws Exception {
			if (message instanceof SubscriptionRequest) {
				getSender().tell(new Restart(), getSelf());
			} else if (message instanceof Recover) {
				System.out.println("Service was recovered");
				preRestart(null, null);
			} else {
				unhandled(message);
			}

		}
	};

	@Override
	public void onReceive(Object message) throws Exception {
		// Requested approvel
		if (message instanceof SubscriptionRequest) {
			System.out.println("Message recieved" + message
					+ ". Tasks in a queue= " + nTasks);
			// if counter is max_counter, device can't process message, send it
			// back
			// in reality if the actor is busy, all these messages would be put
			// in a queue, so no need in that
			if (nTasks >= MAX_COUNTER) {
				System.out.println("BUSY");
				getSender().tell(new Busy(), getSelf());
			} else {
				// simulate "hard work". Approvement would be set only after
				// delay
				context()
						.system()
						.scheduler()
						.scheduleOnce(
								Duration.create(WORK_DELAY, TimeUnit.SECONDS),
								getSender(),
								new SubscriptionApprovement(APPROVE_MESSAGE),
								context().system().dispatcher(), getSelf());
				nTasks++;
			}

		}
		// Recieve the subscription approval decrease counter
		else if (message instanceof SuccessfulApprove) {
			nTasks--;
			System.out
					.println("Recieve Successuful approve. Tasks in a queue ="
							+ nTasks);

		}
		// here we simulate that something we get wrong message and we need to
		// send an error message back
		// so he Subscription can recover this error itself
		// Also change provider behaviour so it will return restart message
		// simulating
		// it need some time to restart, after that provider will be restarted
		// and
		// subsciptions would be approved
		else if (message instanceof SimulatedErrorMessage) {
			System.out
					.println("Provider recieved bad message. Provider will be restarted."
							+ " All unfinnished subscriptions should be restarted too");
			getContext().become(restart);
			getSender().tell(new ProviderFail(), getSelf());

		} else {
			unhandled(message);
		}

	}
}
