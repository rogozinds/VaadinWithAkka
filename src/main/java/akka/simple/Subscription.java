package akka.simple;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;

import com.example.MyVaadinUI;
import com.example.UIProvider;
import com.example.akka_osgi.Broadcaster;
import com.sun.net.ssl.internal.ssl.Provider;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Subscription extends UntypedActor {
	private int WAIT_TIMEOUT=5;
	String name;
	public Subscription(String name) {
		this.name=name;
	}
	@Override
	public void onReceive(Object message) throws Exception {
		//if phone is busy, emulate pause by sending same request to phone after timeout
		if (message instanceof PHONE_BUSY) {
			System.out.println("REcieve phone is busy");
	     	context().system().scheduler().scheduleOnce(Duration.create(WAIT_TIMEOUT, TimeUnit.SECONDS), getSender(), new RequestApproving(),context().system().dispatcher(),getSelf());
	     	  
		}
	    else if(message instanceof SubscriptionApprovement) {
	    	System.out.println("GOT APPROVEMENT");
	    	getSender().tell(new SuccessfulApprove(), null);
	    	Broadcaster.broadcast(name+((SubscriptionApprovement)message).getMessage());
		}
		else {
			unhandled(message);
		}
		
	}

}
