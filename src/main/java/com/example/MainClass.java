package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.simple.PhoneSim;
import akka.simple.RequestApproving;
import akka.simple.Subscription;

public class MainClass {
	static public void main(String[] args ) {
		startsActors();
	}
    static private void startsActors() {
    	System.out.println("Started");
    	final ActorSystem system = ActorSystem.create("dummy-messages");
    	final ActorRef phone=system.actorOf(Props.create(PhoneSim.class));
    	final ActorRef actorSubscription=system.actorOf(Props.create(Subscription.class));
    	phone.tell(new RequestApproving() , actorSubscription);
    }
}
