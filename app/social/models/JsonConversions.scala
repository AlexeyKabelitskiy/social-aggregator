package social.models

import play.api.libs.json.{Json, JsValue, Writes}
import social.models.FeedItem

/**
 */
object JsonConversions {
  implicit val feedItemWrites = new Writes[FeedItem] {
    override def writes(o: FeedItem): JsValue = Json.obj(
      "id"->Json.toJson(o.id),
      "time"->Json.toJson(o.time.getMillis),
      "author"->Json.toJson(o.author),
      "action"->Json.toJson(o.action),
      "object"->Json.toJson(o.objectName),
      "comment"->Json.toJson(o.comment),
      "type"->Json.toJson(o.t.name)
    )
  }

  implicit val notificationsWrites = new Writes[Notification] {
    override def writes(o : Notification): JsValue = Json.obj(
      "id" -> Json.toJson(o.id),
      "priority" -> Json.toJson(o.priority.value),
      "timestamp" -> Json.toJson(o.timestamp.getMillis),
      "message" -> Json.toJson(o.message)
    )
  }

}
