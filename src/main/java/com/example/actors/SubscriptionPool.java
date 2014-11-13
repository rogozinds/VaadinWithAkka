package com.example.actors;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.example.messages.SubscriptionRequest;

/**
 * This class is a subscriptionPool actor, every subscription is created as a
 * child actor of this class.
 *
 */
public class SubscriptionPool extends UntypedActor {
	int nSubs = 0;
	final int TIMEOUT = 1;
	ActorSystem system;
	ActorRef provider;

	public SubscriptionPool(int nSubs, ActorRef provider) {
		super();
		this.system = context().system();
		this.nSubs = nSubs;
		this.provider = provider;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// if Subscription request create nSubs actors and
		// start
		if (message instanceof SubscriptionRequest) {
			startApprovingSubscriptions();
		}

	}

	private void startApprovingSubscriptions() {
		for (int i = 0; i < nSubs; i++) {
			ActorRef subscription = system.actorOf(Props.create(
					Subscription.class, "Subscription" + i));
			// sends request of subscription every TIMEOUT seconds
			// sends request from SubscriptionActor to PhoneActor
			system.scheduler().scheduleOnce(
					Duration.create(TIMEOUT, TimeUnit.SECONDS), provider,
					new SubscriptionRequest(), system.dispatcher(),
					subscription);
		}
	}
}
