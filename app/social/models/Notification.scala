package social.models

import org.joda.time.DateTime
import social.utils.EnumLike

/**
 * System notification, which is shown on UI
 */

case class Notification(id: Int, timestamp: DateTime, priority: Priority, message: String)

case class Priority (value: Int, name: String)

object Priority extends EnumLike[Priority]{
  val Debug = Priority(1, "Debug")
  val Info = Priority(2, "Info")
  val Warning = Priority(3, "Warning")
  val Error = Priority(4, "Error")
  
  override val values = Seq(Debug, Info, Warning, Error)
}