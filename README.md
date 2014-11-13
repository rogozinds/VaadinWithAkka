VaadinWithAkka
==============

A Tutorial project how to combine akka and Vaadin. Basically this is a variation of a classical producer consumer problem. Here we have several producers and one concumer or the other way around - depends how you look at it :).

The main idea:
We have one service provider, which approve subscriptions (requests). We request several subscriptions from UI,
each subscription is an actor (thread safe task). Provider can work simulteneosly only with fixed amount of tasks,
if the limis is exceeded Provider sends a message to a subscription, that subscription should request again after
some timeout. The info about approved subscriptions is send to UI asynchronously (via push).


