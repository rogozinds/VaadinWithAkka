package com.example;

import com.example.akka_osgi.Broadcaster;
import com.vaadin.ui.UI;

public class UIProvider {

	MyVaadinUI ui;
	public UIProvider(MyVaadinUI ui) {
		this.ui=ui;
	}
	public void approveSubscription() {
		System.out.println("Subscription approved");
	}
}
