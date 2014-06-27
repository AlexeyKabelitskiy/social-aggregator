package social.models

import org.joda.time.DateTime

/**
 * System notification, which is shown on UI
 */

class Notification(val id: Int, val timestamp: DateTime, val priority: Priority, val message: String) {


}


class Priority (val value: Int, val name: String)

object Priority {  
  object Debug extends Priority(1, "Debug")
  object Info extends Priority(2, "Info")
  object Warning extends Priority(3, "Warning")
  object Error extends Priority(4, "Error")
  
  val values = Array(Debug, Info, Warning, Error)
}