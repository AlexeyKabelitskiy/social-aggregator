package social.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import play.Logger
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Concurrent, Enumerator, Input, Iteratee}
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.{JsValue, Writes}
import play.api.mvc.WebSocket
import play.api.Play.current
import social.models.ServerEvent
import scala.concurrent.duration._
import akka.util.Timeout

/**
 * Creates Web Sockets actors.
 */

object WebSocketActor {

  //wait for web socket to start
  val TIMEOUT = 5.seconds

  def websocket(in: Props, out: Seq[Props]): WebSocket[JsValue] = WebSocket[JsValue] { request => (theirIn, theirOut) =>{
    val supervisor = Akka.system.actorOf(Props(classOf[WebSocketSupervisor], in, out))
    //Get two answers from supervisor actor and setup Iteratee/Enumerator for WebSocket data
    implicit val timeout = Timeout(TIMEOUT)
    implicit val ec = Akka.system.dispatcher
    for(
      consumer <- (supervisor ? GetConsumer).mapTo[ActorRef]; //TODO Exception handling??
      enumerator <- (supervisor ? GetEvents).mapTo[Enumerator[JsValue]]
    ) yield {
      val iteratee = Iteratee.foreach[JsValue](consumer ! _).map(_ => Akka.system.stop(supervisor))
      theirIn |>>> iteratee
      enumerator |>> theirOut
    }
  }}

  def websocket(out: Seq[Props]): WebSocket[JsValue] = websocket(Props[LoggingConsumer], out)

  object GetConsumer
  object GetEvents
}

object WebSocketSupervisor {
  case class EventsFor(originator: ActorRef)
  case class Responder(responder: ActorRef)
}

/**
 * WebSocket supervisor actor. One per WS connection. Manages life-cycle of all worker actors
 * @param consumerProps - consumer factory
 * @param workersProps - sequence of worker actors' factories
 * @param names - sequence of optional names for worker actors
 */
class WebSocketSupervisor(consumerProps: Props, workersProps: Seq[Props], names: Seq[Option[String]]) extends Actor{

  def this(cProps: Props, wProps: Seq[Props]) = this(cProps, wProps, List.fill(wProps.size)(None))

  //Write to WS
  val responder = context.actorOf(Props[WebSocketResponder], "WSResponder")
  //Reads from WS
  val consumer = context.actorOf(consumerProps, "WSConsumer")
  //Supplies data for responder
  //Zip actorRef with corresponding name. If name is absent use system generated
  val workers: Seq[ActorRef] = workersProps.zip(names) map {
    case (ref, Some(name)) => context.actorOf(ref,name)
    case (ref, None)  => context.actorOf(ref)
  }

  import WebSocketActor._
  import WebSocketSupervisor._

  @throws(classOf[Exception])
  override def preStart(): Unit = {
    Logger.info("Web socket started")
    workers.foreach(_ ! Responder(responder))
  }

  override def postStop(): Unit = {
    Logger.info("Web socket stopped")
  }

  def receive: Receive = {
    case GetConsumer => sender ! consumer
    case GetEvents => responder ! EventsFor(sender)
  }
}

class WebSocketResponder extends Actor  {
  import WebSocketSupervisor._
  //Implicit JSON conversions
  import social.models.JsonConversions._
  //Perform all async operations on actor's ExecutionContext
  import context.dispatcher

  var channel : Option[Channel[JsValue]] = None
  val enumerator = Concurrent.unicast(
    (ch: Channel[JsValue]) => channel = Some(ch),
    done(),
    error
  )

  override def receive: Actor.Receive = {
    case n: ServerEvent[_] => respond(n)
    case EventsFor(originator) => originator ! enumerator
  }

  def respond(value: ServerEvent[_])(implicit wr: Writes[ServerEvent[_]]): Unit = {
    Logger.trace("Sending WS event")
    channel.foreach(_.push(wr.writes(value)))
  }

  def done(): Unit = {
    Logger.debug("Enumerator completed")
  }

  def error(msg: String, element: Input[JsValue]): Unit = {
    Logger.error("Enumerator error. "+msg)
  }

}

class LoggingConsumer extends Actor {
  override def receive: Actor.Receive = {
    case a: Any => Logger.debug(s"Received message from WebSocket: ${a.toString}")
  }
}