package social.actors

import scala.concurrent.duration._

import akka.actor.{Actor, ActorRef, Cancellable}
import org.joda.time.DateTime
import play.api.Logger
import social.controllers.Mock
import social.models.{EventType, Notification, ServerEvent}

/**
 * Generator of fake notifications. Will accept notification events in future
 */
object NotificationWorker {
  val DEGRADATION_TIMES = 10
  object Sync
}


class NotificationWorker(var lastFetched: DateTime) extends Actor{
  import NotificationWorker._
  import context.dispatcher

  var alreadyScheduled = 0

  var responder: Option[ActorRef] = None
  var nextExecution: Option[Cancellable] = None

  override def receive: Actor.Receive = {
    case WebSocketSupervisor.Responder(ref) =>
      responder = Some(ref)

    case Sync =>
      val now = DateTime.now()
      val nf = fetch(lastFetched, now)
      lastFetched = now
      Logger.info(s"[${self.path}}] Fetched ${nf.size} notifications")

      for {
        n <- nf
        r <- responder
      } yield r ! ServerEvent(EventType.Notification, n)

      reSchedule()

    case _@a => Logger.error("Unknown NF: " + a.toString)
  }

  def fetch(time: DateTime, time1: DateTime): Seq[Notification] = {
    Seq(Mock.randomNotifications(currentTstmp = true))
  }


  @throws(classOf[Exception])
  override def preStart(): Unit = {
    reSchedule()
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

  def reSchedule(): Unit = {
    val task = context.system.scheduler.scheduleOnce(delay(alreadyScheduled), self, Sync)
    nextExecution = Some(task)
    alreadyScheduled += 1
  }

  def delay(times: Int): FiniteDuration = {
    if(times < NotificationWorker.DEGRADATION_TIMES)
      2.seconds
    else
      1.minute
  }

}
