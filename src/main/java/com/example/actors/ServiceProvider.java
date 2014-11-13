package com.example.actors;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;

import com.example.messages.Busy;
import com.example.messages.SubscriptionApprovement;
import com.example.messages.SubscriptionRequest;
import com.example.messages.SuccessfulApprove;

public class ServiceProvider extends UntypedActor {
	final private int WORK_DELAY = 3;
	final private int MAX_COUNTER = 3;
	static private int nTasks = 0;
	final String APPROVE_MESSAGE = "approved";

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

		} else {
			unhandled(message);
		}

	}

}
