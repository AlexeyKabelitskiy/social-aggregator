social-aggregator
=================

Agregates news from different social networks<br>
Agregator for concrete network is a plugin (Play! OAuth)<br>
There is a settings page with a list of social networks to poll. Each network has a list properties, specific for it.<br>
Settings for exact user are stored in DB (for example, which social neworks are connected) - MongoDB<br>
Server is periodically polled  (possibly, Web sockets) for updates, news on page is autoupdated<br>
all news sources polled at this time asynchronously (akka)<br> 
