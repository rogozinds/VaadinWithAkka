package com.example;

import javax.servlet.annotation.WebServlet;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.example.Broadcaster.BroadcastListener;
import com.example.actors.ServiceProvider;
import com.example.actors.SubscriptionPool;
import com.example.messages.Recover;
import com.example.messages.SimulatedErrorMessage;
import com.example.messages.SubscriptionRequest;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@Push
public class MyVaadinUI extends UI implements BroadcastListener {
	public VerticalLayout mainLayout = new VerticalLayout();
	final int N_SUBSCRIPTIONS = 10;
	public VerticalLayout leftLayout = new VerticalLayout();
	public VerticalLayout rightLayout = new VerticalLayout();
	public HorizontalSplitPanel mainPanel = new HorizontalSplitPanel(
			leftLayout, rightLayout);
	public TextArea requestedSubsTextArea = new TextArea(
			"Requested subscriptions");
	private final ActorSystem actorSystem = ActorSystem
			.create("dummy-messages");
	ActorRef subscriptionPool;
	ActorRef servProvider;

	@WebServlet(value = { "/*", "/VAADIN/*", "/MyVaadinUI/*" }, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.example.AppWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		buildLayout(mainLayout);
		Broadcaster.register(this);
		initActors();

	}

	// Must also unregister when the UI expires
	@Override
	public void detach() {
		Broadcaster.unregister(this);
		super.detach();
	}

	private void initActors() {
		final ActorRef servProvider = actorSystem.actorOf(Props
				.create(ServiceProvider.class));

		// Create a subscription pool actor, all subscriptions will be created
		// in that class.
		subscriptionPool = actorSystem.actorOf(Props.create(
				SubscriptionPool.class, N_SUBSCRIPTIONS, servProvider));
	}

	private void startsActors() {
		final int N_SUBSCRIPTIONS = 10;
		String[] subsNames = new String[N_SUBSCRIPTIONS];
		for (int i = 0; i < N_SUBSCRIPTIONS; i++) {
			subsNames[i] = "Subscription " + i;
			requestedSubsTextArea.setValue(requestedSubsTextArea.getValue()
					+ "Requested: " + subsNames[i] + "\n");
		}
		// Send a message to the subscription pool and let it decide what to do
		// :)
		subscriptionPool.tell(new SubscriptionRequest(), null);

	}

	private void buildLayout(VerticalLayout layout) {
		layout.setMargin(true);
		setContent(layout);
		mainPanel.setSplitPosition(35, Sizeable.Unit.PERCENTAGE);
		layout.addComponent(mainPanel);
		Button button = new Button("Request subscriptions");
		button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				startsActors();
			}
		});
		Button btnError = new Button("Simulate error");
		btnError.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				simulateError();
			}
		});
		Button btnRecover = new Button("Simulate recover");
		btnRecover.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				simulateRecover();
			}
		});
		leftLayout.addComponent(button);
		leftLayout.addComponent(btnError);
		leftLayout.addComponent(btnRecover);
		requestedSubsTextArea.setWidth("500px");
		requestedSubsTextArea.setHeight("600px");
		rightLayout.addComponent(requestedSubsTextArea);
	}

	private void simulateRecover() {
		subscriptionPool.tell(new Recover(), null);
	}

	private void simulateError() {
		subscriptionPool.tell(new SimulatedErrorMessage(), null);
	}

	@Override
	public void receiveBroadcast(final String message) {
		access(new Runnable() {
			@Override
			public void run() {
				leftLayout.addComponent(new Label(message));
			}
		});

	}
}
