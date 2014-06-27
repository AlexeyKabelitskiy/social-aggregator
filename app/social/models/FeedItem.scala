package social.models

import org.joda.time.DateTime
import play.api.libs.json.{Json, JsValue, Writes}

/**
 */
class FeedItem (val id: String, val time: DateTime, val author: String, val action: String, val objectName: String, val comment: String, val t: ItemType) {

}

trait ItemType{
   def name: String
}

case class VK(override val name: String = "vk") extends ItemType
case class FB(override val name: String = "fb") extends ItemType

object FeedItem {
  def vk(id: String, time: DateTime, author: String, action: String, objectName: String, comment: String) =
    new FeedItem(id,time,author,action,objectName,comment,VK())

  def fb(id: String, time: DateTime, author: String, action: String, objectName: String, comment: String) =
    new FeedItem(id,time,author,action,objectName,comment,FB())

}

