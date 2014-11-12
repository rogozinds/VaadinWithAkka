package akka.simple;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;

public class PhoneSim extends UntypedActor {
	final private int WORK_DELAY=3;
	final private int MAX_COUNTER=3;
	static private int counter=0;
	@Override
	public void onReceive(Object message) throws Exception {
		
		String response="Approved";
		//Requasted approvel
		if(message instanceof RequestApproving) {
			System.out.println("Message recieved"+message + "counter "+counter);
			//if counter is max_counter, device can't process message, send it back
			// in reality if the actor is busy, all these messages would be put in a queue, so no need in that
			if (counter>=MAX_COUNTER) {
				System.out.println("PHONE IS BUSY");
				getSender().tell(new PHONE_BUSY(), getSelf());
			}
			else {
				//getSender().tell(new SubscriptionApprovement(response), getSelf());
				//emulate "hard work" approvement would be set only after delay
			   	context().system().scheduler().scheduleOnce(Duration.create(WORK_DELAY, TimeUnit.SECONDS),
			   			getSender(), new SubscriptionApprovement(response),context().system().dispatcher(),getSelf());
				counter++;
			}
		
		}
		//Recieve the subscription approval decrease counter
		else if (message instanceof SuccessfulApprove) {
			counter--;
			System.out.println("Recieve Successuful approve. Counter ="+counter);
			
		}
		else {
			unhandled(message);
		}

	}

}
