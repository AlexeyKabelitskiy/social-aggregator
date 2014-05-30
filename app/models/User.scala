package models

import org.joda.time.DateTime

/**
 * Logged in user
 */
class User(val login: String, val lastSeenNotification: DateTime) {

}
