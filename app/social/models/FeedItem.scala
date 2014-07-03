package social.models

import org.joda.time.DateTime
import social.utils.EnumLike

/**
 */
class FeedItem (val id: String, val time: DateTime, val author: String, val action: String, val objectName: String, val comment: String, val t: ItemType)

case class ItemType(name: String)
object ItemType extends EnumLike[ItemType]{
  val VK = ItemType("vk")
  val FB = ItemType("fb")

  override val values = Seq(VK, FB)
}

object FeedItem {
  def vk(id: String, time: DateTime, author: String, action: String, objectName: String, comment: String) =
    new FeedItem(id,time,author,action,objectName,comment,ItemType.VK)

  def fb(id: String, time: DateTime, author: String, action: String, objectName: String, comment: String) =
    new FeedItem(id,time,author,action,objectName,comment,ItemType.FB)

}

