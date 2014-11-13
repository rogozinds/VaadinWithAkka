package com.example.actors;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;

import com.example.Broadcaster;
import com.example.messages.Busy;
import com.example.messages.ProviderFail;
import com.example.messages.Restart;
import com.example.messages.SubscriptionApprovement;
import com.example.messages.SubscriptionRequest;
import com.example.messages.SuccessfulApprove;

public class Subscription extends UntypedActor {
	private int WAIT_TIMEOUT = 5;
	private int RESTART_TIMEOUT = 10;
	String name;

	public Subscription(String name) {
		this.name = name;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// if provider is busy, emulate pause by sending same request to
		// provider
		// after timeout
		if (message instanceof Busy) {
			System.out.println("Recieve phone is busy");
			retryAfterTimeout(WAIT_TIMEOUT);
		} else if (message instanceof Restart) {
			System.out.println("Service is restarting.");
			retryAfterTimeout(RESTART_TIMEOUT);
		} else if (message instanceof SubscriptionApprovement) {
			System.out.println("GOT APPROVEMENT");
			getSender().tell(new SuccessfulApprove(), null);
			Broadcaster.broadcast(name + " "
					+ ((SubscriptionApprovement) message).getMessage());
		} else if (message instanceof ProviderFail) {
			// got a provider fail message should change behaviour of the actor
			System.out.println("Got provider fail for " + name);

		} else {
			unhandled(message);
		}

	}

	private void retryAfterTimeout(int timeout) {
		context()
				.system()
				.scheduler()
				.scheduleOnce(Duration.create(timeout, TimeUnit.SECONDS),
						getSender(), new SubscriptionRequest(),
						context().system().dispatcher(), getSelf());
	}
}
