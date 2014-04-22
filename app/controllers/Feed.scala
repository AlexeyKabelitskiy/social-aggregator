package controllers

import com.github.nscala_time.time.Imports._

import java.util.Random

import models._
import models.JsonConversions._

import org.joda.time.DateTime

import play.api.mvc._
import play.api.libs.json.Json

/**
 * Feed controller
 */
object Feed extends Controller{
  def feed = Action{
    val count = Mock.g.nextInt(15)+1;
    val feed = for(i <- 1 to count) yield Mock.randomFeed
    Ok(Json.toJson(feed.sortWith((left: FeedItem, right: FeedItem) => left.time > right.time)))
  }
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

  def randomFeed = {
    val id = this.synchronized{
      lastId =lastId+1
      lastId
    }
    new FeedItem(id.toString,randomDate, random(names) + " " + random(surnames),random(action),random(objectType),random(comments),randomType)
  }

}