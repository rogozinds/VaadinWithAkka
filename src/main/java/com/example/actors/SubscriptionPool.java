package com.example.actors;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.example.messages.Recover;
import com.example.messages.SimulatedErrorMessage;
import com.example.messages.SubscriptionRequest;

/**
 * This class is a subscriptionPool actor, every subscription is created as a
 * child actor of this class.
 *
 */
public class SubscriptionPool extends UntypedActor {
	int nSubs = 0;
	final int TIMEOUT = 1;
	final int ERROR_TIMEOUT = 4;
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
			System.out.println("Request approve send");
			startApprovingSubscriptions();
		} else if (message instanceof SimulatedErrorMessage) {
			// Make random subscription to send an error message
			int errorIndex = 3;
			ActorRef errorSubscription = system.actorOf(Props.create(
					Subscription.class, "Subscription" + errorIndex));
			provider.tell(message, errorSubscription);
		}
		// Simulate recover of the service provider
		else if (message instanceof Recover) {
			System.out.println("Pool tryed to recover Provider");
			provider.tell(message, null);
		} else {
			unhandled(message);
		}
	}

	private void startApprovingSubscriptions() {
		ActorRef[] subscriptions = new ActorRef[nSubs];
		for (int i = 0; i < nSubs; i++) {
			subscriptions[i] = system.actorOf(Props.create(Subscription.class,
					"Subscription" + i));
			// sends request of subscription every TIMEOUT seconds
			// sends request from SubscriptionActor to PhoneActor
			system.scheduler().scheduleOnce(
					Duration.create(TIMEOUT, TimeUnit.SECONDS), provider,
					new SubscriptionRequest(), system.dispatcher(),
					subscriptions[i]);
		}

	}
}
