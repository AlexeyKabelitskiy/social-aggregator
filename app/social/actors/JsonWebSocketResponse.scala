package social.actors

import play.api.libs.iteratee.{Input, Concurrent}
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.{JsValue, Writes}
import akka.actor.Actor
import akka.actor.Actor.Receive
import play.Logger
import views.html.helper.input

/**
 */

trait JsonResponder[T]{
  def response(value: T)(implicit wr: Writes[T]): Unit
  def close(): Unit
}

class JsonWebSocketResponse[T] extends JsonResponder[T]{

  import play.api.libs.concurrent.Execution.Implicits._

  var channel : Option[Channel[JsValue]] = None
  val enumerator = Concurrent.unicast(
    (ch: Channel[JsValue]) => channel = Some(ch),
    done(),
    error
  )

  def response(value: T)(implicit wr: Writes[T]): Unit = {
    Logger.info("Sending WS event")
    channel.foreach(_.push(wr.writes(value)))
  }

  def close(): Unit = {
    channel.foreach(_.eofAndEnd())
  }

  def done(): Unit = {
    Logger.info("Enumerator completed")
  }

  def error(msg: String, element: Input[JsValue]): Unit = {
    Logger.error("Enumerator error. "+msg)
  }
}
