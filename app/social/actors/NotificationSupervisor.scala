package social.actors

import social.models.Notification
import akka.actor._
import org.joda.time.DateTime
import akka.routing.FromConfig
import scala.concurrent.duration._
import social.controllers.Mock
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.json.JsValue
import play.api.Logger
import play.api.Play.current

/**
 */

object NotificationSupervisor {

  def websocket(lastStamp: DateTime): (Iteratee[JsValue, _], Enumerator[JsValue]) = {

    import play.api.libs.concurrent.Execution.Implicits._

    val responder = new JsonWebSocketResponse[Notification]
    val actor = Akka.system.actorOf(Props(classOf[NotificationSupervisor], responder,lastStamp))
    val in = Iteratee.foreach[JsValue]{ value =>
      Logger.info("Interatee triggered: "+value)
    }.map{ _ =>
      actor ! PoisonPill
      Logger.info("Notification websocket closed")
    }
    actor ! Start
    (in, responder.enumerator)
  }

  object Start
}

class NotificationSupervisor(val responder: JsonResponder[Notification], lastFetched: DateTime) extends Actor{
  import NotificationSupervisor._

  val responderActor = context.actorOf(Props(classOf[Responder], responder), "NotificationResponder")
  val sync = context.actorOf(Props(classOf[NotificationSync], responderActor, lastFetched), "NotificationSync")
    //context.system.actorOf(Props[NotificationSync].withRouter(FromConfig("dispatcherId")))

  context.watch(responderActor)
  context.watch(sync)

  override def postStop() {
     responder.close()
  }

  override def receive: Receive = {
    case NotificationSupervisor.Start =>
      //Execution context must be changed in case of blocking sync operation
      sync ! Start
      context.become(process)
    case Terminated(ref) => Logger.info(s"${ref.path} terminated")
  }

  def process: Receive = {
    case Terminated(ref) => Logger.info(s"${ref.path} terminated")
    case _ => Logger.warn("Unknown message")
  }
}

class Responder (val responder: JsonResponder[Notification]) extends Actor  {
  import social.models.JsonConversions._
  override def receive: Actor.Receive = {
    case n: Notification => responder.response(n)
  }
}


object NotificationSync {
  val DEGRADATION_TIMES = 10
  object Sync
}


class NotificationSync(val responder: ActorRef, var lastFetched: DateTime) extends Actor{
  import NotificationSync._

  var alreadyScheduled = 0

  var nextExecution: Option[Cancellable] = None

  def reSchedule(): Unit = {
    val task = context.system.scheduler.scheduleOnce(delay(alreadyScheduled), self, Sync)(Akka.system.dispatchers.lookup("play.akka.actor.default-dispatcher"))
    nextExecution = Some(task)
    alreadyScheduled += 1
  }

  override def receive: Actor.Receive = {
    case NotificationSupervisor.Start =>
      context.become(process)
      reSchedule()
  }

  def process: Receive = {
    case NotificationSync.Sync =>
      val now = DateTime.now()
      val nf = fetch(lastFetched, now)
      lastFetched = now
      Logger.info(s"[${self.path}}] Fetched ${nf.size} notifications")
      nf.foreach(responder ! _)
      reSchedule()
  }

  def fetch(time: DateTime, time1: DateTime): Seq[Notification] = {
    Seq(Mock.randomNotifications(currentTstmp = true))
  }

  @throws(classOf[Exception])
  override def postStop(): Unit = {
    nextExecution.foreach{ _.cancel() }
  }


  @throws(classOf[Exception])
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    nextExecution.foreach(_.cancel())
    nextExecution = None
  }


  @throws(classOf[Exception])
  override def postRestart(reason: Throwable): Unit = {
    reSchedule()
  }

  def delay(times: Int): FiniteDuration = {
    if(times < NotificationSync.DEGRADATION_TIMES)
      2.seconds
    else
      1.minute
  }

}

