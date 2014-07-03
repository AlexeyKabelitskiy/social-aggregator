##social-aggregator
=================

Agregates news from different social networks

Agregator for concrete network is a plugin (Play! OAuth)

There is a settings page with a list of social networks to poll. Each network has a list properties, specific for it.

Settings for exact user are stored in DB (for example, which social neworks are connected) - MongoDB

Server is periodically polled  (possibly, Web sockets) for updates, news on page is autoupdated

all news sources polled at this time asynchronously (akka)
 
**Features (~~Done~~):**
- ~~play project~~
- ~~UI frameworks (backbone, requirejs,twitter, moment.js)~~
- ~~basic logging~~
- notifications (web sockets, akka)
  - ~~server-side web socket~~
  - ~~basic ui notification~~
  - ~~common component for server WS~~
  - ~~single WS per app (UI events and mixed-content WS)~~
  - advanced UI (highlight new items with fading)
  - mongo setup
  - akka events hub
  - akka tests
- logging and troubleshooting support
- authorization
- user settings (mongo)
- social server polling (akka, mongo, web socket)
	- basic akka actors
	- write feed to mongo
- distributed polling - standalone akka actors, embedded actor as fallback 
- connect to fb
- design plugin framework
- connect to vk
- load testing