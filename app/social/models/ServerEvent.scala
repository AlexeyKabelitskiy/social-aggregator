package social.models

import social.utils.EnumLike

/**
 * Wrapper object for models sent over WS
 */

case class EventType(name: String)

object EventType extends EnumLike[EventType]{
  val Notification = EventType("Notification")
  val FeedItem = EventType("FeedItem")
  override val values = Seq(Notification, FeedItem)
}

case class ServerEvent[T] (eventType: EventType, payload: T)
