package views

import controllers._

/**
 */
object ViewUtils {
  def atWebJarsJS(path: String):String = {
    val resolvedPath = routes.WebJarAssets.at(WebJarAssets.locate(path)).toString()
    if(resolvedPath.endsWith(".js"))
      resolvedPath.substring(0,resolvedPath.length - 3)
    else
      resolvedPath
  }
}
