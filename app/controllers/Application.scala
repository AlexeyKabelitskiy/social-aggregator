package controllers

//implicits
import models.JsonConversions._
import com.github.nscala_time.time.Imports._

import models._
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.Future
import utils.Logging
import java.util.Random
import org.joda.time.DateTime

object Application extends Controller with Logging {

  override val loggerName = "controllers.Application"

  case class LongAction[A]( action: Action[A]) extends Action[A]{
    override def apply(request: Request[A]): Future[SimpleResult] = {
      val start = System.currentTimeMillis()
      val ret = action.apply(request)
      val diff = 1000  - (System.currentTimeMillis() - start)
      if(diff > 0) {
        val fail = Mock.randomLength(10) > 7
        if(fail) {
          Thread.sleep(Mock.randomLong(diff))
          throw new IllegalArgumentException
        } else {
          Thread.sleep(diff)
        }
      }
      ret
    }
    lazy val parser = action.parser
  }

  def index = Action {
    Ok(views.html.index())
  }

  def feed = LongAction{ Action{
    val count = Mock.randomLength()
    val feed = for(i <- 1 to count) yield Mock.randomFeed
    logger.debug(s"Generated $count feed items")
    Ok(Json.toJson(feed.sortWith((left: FeedItem, right: FeedItem) => left.time > right.time)))
  }}

  def notifications = LongAction { Action {
    val count = Mock.randomLength()
    val notifications = for(i <- 1 to count) yield Mock.randomNotifications
    logger.debug(s"Generated $count notifications")
    Ok(Json.toJson(notifications.sortWith((left: Notification, right: Notification) => left.timestamp > right.timestamp)))

  }}

}

object Mock {
  val g = new Random()

  private var lastId = 0

  val names = Array("Jack", "Joe", "Alex","Victor","Ann","Alice","July")
  val surnames = Array("Smith", "Burns", "McDonald","Duck","Larsen","Wachovsky","Ragan")
  val action = Array("likes", "commented")
  val objectType = Array("photo", "post", "page")
  val comments = Array("Cool!", "They are nice!", "Disgusting :(", "Great! That's exactly what I want", "Why do you think so?")

  def random[T](content: Array[T]): T = {
    val idx = g.nextInt(content.length)
    content(idx)
  }

  def randomType = random(Array(VK(),FB()))

  def randomDate = DateTime.now - g.nextInt(48).hours

  def randomLong(max: Long) = g.nextInt(max.toInt).toLong

  def randomLength(max: Int = 15) = g.nextInt(max) + 1

  def randomFeed = {
    val id = this.synchronized{
      lastId =lastId+1
      lastId
    }
    new FeedItem(id.toString,randomDate, random(names) + " " + random(surnames),random(action),random(objectType),random(comments),randomType)
  }

  def randomNotifications = {
    val id = this.synchronized{
      lastId =lastId+1
      lastId
    }
    new Notification(id,randomDate,random(Priority.values),random(comments))
  }

}
