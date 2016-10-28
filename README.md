Vaadin With Akka
==============
# Description
A Tutorial project how to combine akka and Vaadin. Basically this is a variation of a classical producer consumer problem. Here we have several producers and one concumer or the other way around - depends how you look at it :).

## The main idea:
We have one service provider, which approve subscriptions (requests). We request several subscriptions from UI,
each subscription is an actor (thread safe task). Provider can work simulteneosly only with fixed amount of tasks,
if the limis is exceeded Provider sends a message to a subscription, that subscription should request again after
some timeout. The info about approved subscriptions is send to UI asynchronously (via push). 
The detailed log can be seen in the System.out 

# Setup and run
## Install dependencies
mvn clean install

## Start jetty server
mvn jetty:run

## Use
Go to http://localhost:8080/, pressing request subscriptions will start serveral Akka actors, that send a subscription request to the mock service. A mock service (also actor) can handle only limited amount of requests Inow three) at a time. The service will respond with BUSY message in this case. The subscription actor will schedule another request, so it will be eventually handled by the service. So, eventually you would get a response in UI, that all you requests were approved.


