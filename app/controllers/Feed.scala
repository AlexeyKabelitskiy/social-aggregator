package controllers

import play.api.mvc._

/**
 * Feed controller
 */
object Feed extends Controller{
  def feed = Action{
    Ok(views.html.index())
  }
}
