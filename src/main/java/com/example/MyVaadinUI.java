package com.example;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.annotation.WebServlet;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.simple.PhoneSim;
import akka.simple.RequestApproving;
import akka.simple.Subscription;

import com.example.akka_osgi.Broadcaster;
import com.example.akka_osgi.Broadcaster.BroadcastListener;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@Push
public class MyVaadinUI extends UI implements BroadcastListener
{
	public VerticalLayout layout=new VerticalLayout();
	private final ActorSystem actorSystem = ActorSystem.create("dummy-messages");
	public Label lbl=new Label("");
	@WebServlet(value = {"/*", "/VAADIN/*", "/MyVaadinUI/*" }, asyncSupported = true)
	
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.example.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	public ActorSystem getActorSystem() {
		return actorSystem;
	}
    @Override
    protected void init(VaadinRequest request) {
        layout.setMargin(true);
        setContent(layout);
        
        Button button = new Button("Request subscriptions");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                startsActors();
            }
        });
        layout.addComponent(button);
        Broadcaster.register(this);
        
    }
    // Must also unregister when the UI expires    
    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    private void startsActors() {
    	final int  N_SUBSCRIPTIONS=10;
    	final int TIMEOUT=1;
    	
    	final ActorRef phone=actorSystem.actorOf(Props.create(PhoneSim.class));
    	String [] subsNames=new String[N_SUBSCRIPTIONS];
    	ActorRef[] subscriptions=new ActorRef[N_SUBSCRIPTIONS];
    	for (int i=0;i<N_SUBSCRIPTIONS;i++) {
    		subsNames[i]="Subscription " + i;
    		subscriptions[i]=actorSystem.actorOf(Props.create(Subscription.class,subsNames[i]));
        	//sends request of subscription every TIMEOUT seconds
        	//sends request from SubscriptionActor to PhoneActor
        	actorSystem.scheduler().scheduleOnce(Duration.create(TIMEOUT, TimeUnit.SECONDS), phone, new RequestApproving(),actorSystem.dispatcher(),subscriptions[i]);
    	}
    	

    }
	@Override
	public void receiveBroadcast(final String message) {
        access(new Runnable() {
            @Override
            public void run() {
                layout.addComponent(new Label(message));
            }
        });
		
	}
}
