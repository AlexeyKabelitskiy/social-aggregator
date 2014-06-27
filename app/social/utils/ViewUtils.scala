package social.utils

import controllers._

/**
 */
object ViewUtils {
  /**
   * Find resource in WebJars by its path
   * @param path
   * @return
   */
  def atWebJars(path: String): String = {
    routes.WebJarAssets.at(WebJarAssets.locate(path)).toString()
  }

  /**
   * Find resource in WebJars by its path and strip .js. Useful for RequireJS configuration
   * @param path
   * @return
   */
  def atWebJarsJS(path: String):String = {
    val resolvedPath = atWebJars(path)
    if(resolvedPath.endsWith(".js"))
      resolvedPath.substring(0,resolvedPath.length - 3)
    else
      resolvedPath
  }
}
