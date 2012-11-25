social-aggregator
=================

Agregates news from different social networks
Agregator for concrete network is a plugin (Play! OAuth)
There is a settings page with a list of social networks to poll. Each network has a list properties, specific for it.
Settings for exact user are stored in DB (for example, which social neworks are connected) - MongoDB
Server is periodically polled  (possibly, Web sockets), news on page is autoupdated
all news sources polled at this time asynchronously (akka) 
