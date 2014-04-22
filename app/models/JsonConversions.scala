package models

import play.api.libs.json.{Json, JsValue, Writes}

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

}
