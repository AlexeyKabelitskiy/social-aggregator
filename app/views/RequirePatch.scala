package views

import org.webjars.{RequireJS, WebJarAssetLocator}
import collection.JavaConversions._
import scala.io.Source

/**
  */
object RequirePatch {

  private lazy val requireConfig: String = createConfig

  case class WebJarPrefix(url: String)

  implicit val defaultPrefix = WebJarPrefix("")

  def createConfig: String = {

    val webjarUrlPrefix = implicitly[WebJarPrefix]

    val webjars = new WebJarAssetLocator().getWebJars

    val webjarsVersionsString = new StringBuilder()

    val webjarConfigsString = new StringBuilder()


    webjars.iterator.foreach {
      pair =>

      // assemble the webjar versions string
        webjarsVersionsString.append("'").append(pair._1).append("': '").append(pair._2).append("', ")

        // assemble the webjar config string
        webjarConfigsString.append("\n").append(getWebJarConfig(pair));
    }

    // remove the trailing ", "
    webjarsVersionsString.delete(webjarsVersionsString.length - 2, webjarsVersionsString.length)


    // assemble the JavaScript
    // todo: could use a templating language but that would add a dependency

    val javaScript = "var webjars = {\n" +
      "    versions: { " + webjarsVersionsString.toString + " },\n" +
      "    path: function(webjarid, path) {\n" +
      "        return '" + webjarUrlPrefix + "' + webjarid + '/' + webjars.versions[webjarid] + '/' + path;\n" +
      "    }\n" +
      "};\n" +
      "\n" +
      "var previousRequireCallback;\n" +
      "if(require && require.callback) {\n" +
      "    previousRequireCallback = require.callback;\n" +
      "}" +
      "var require = {\n" +
      "    callback: function() {\n" +
      "        if(previousRequireCallback) previousRequireCallback();" +
      "        // no-op webjars requirejs plugin loader for backwards compatibility\n" +
      "        define('webjars', function () {\n" +
      "            return { load: function (name, req, onload, config) { onload(); } }\n" +
      "        });\n" +
      "\n" +
      "        // all of the webjar configs from their webjars-requirejs.js files\n" +
      webjarConfigsString.toString +
      "    }\n" +
      "}"
    javaScript
  }

  def setupJavaScript(webjarUrlPrefix: String) = {
    implicit val prefix = WebJarPrefix(webjarUrlPrefix)
    requireConfig
  }

  def getWebJarConfig(webjar: (String, String)) = {

    val filename = WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/" + webjar._1 + "/" + webjar._2 + "/" + "webjars-requirejs.js"
    val inputStream = classOf[RequireJS].getClassLoader.getResourceAsStream(filename)
    if (inputStream != null) {

      val config = "// webjar config for " + webjar._1 + "\n" +
        withSource(Source.fromInputStream(inputStream)) {
          s =>
            s.getLines().mkString("\n")
        }

      config
    }

    ""
  }

  def withSource[T, S <: Source](source: S)(f: S => T): T = {
    val value = f(source)
    source.close()
    value
  }

}
