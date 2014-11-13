package com.example.actors;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;

import com.example.Broadcaster;
import com.example.messages.Busy;
import com.example.messages.SubscriptionApprovement;
import com.example.messages.SubscriptionRequest;
import com.example.messages.SuccessfulApprove;

public class Subscription extends UntypedActor {
	private int WAIT_TIMEOUT = 5;
	String name;

	public Subscription(String name) {
		this.name = name;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// if phone is busy, emulate pause by sending same request to phone
		// after timeout
		if (message instanceof Busy) {
			System.out.println("REcieve phone is busy");
			context()
					.system()
					.scheduler()
					.scheduleOnce(
							Duration.create(WAIT_TIMEOUT, TimeUnit.SECONDS),
							getSender(), new SubscriptionRequest(),
							context().system().dispatcher(), getSelf());

		} else if (message instanceof SubscriptionApprovement) {
			System.out.println("GOT APPROVEMENT");
			getSender().tell(new SuccessfulApprove(), null);
			Broadcaster.broadcast(name + " "
					+ ((SubscriptionApprovement) message).getMessage());
		} else {
			unhandled(message);
		}

	}

}
