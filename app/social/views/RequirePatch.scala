package social.views

import scala.io.Source

import collection.JavaConversions._
import org.webjars.{RequireJS, WebJarAssetLocator}

/**
 * Patch for standard library org.webjars.RequireJS#getSetupJavaScript(java.lang.String). Allows preliminary
 * callback object to setup webjar JS libraries which are not AMD-ready
 * <pre>
 *      &lt;script&gt;
 *          //patch for non-AMD moment.js webjar setup
 *          var require = {
 *              callback : function() {
 *                  var momentPath = '@social.utils.ViewUtils.atWebJarsJS("moment.js")';
 *                  requirejs.config({
 *                      paths: { "moment": momentPath},
 *                      shim: { "moment": { "exports": "moment" } }
 *                  });
 *              }
 *          };
 *          @ Html(social.views.RequirePatch.setupJavaScript("/js/lib/"))
 *      &lt;/script&gt;
 * </pre>
 */
object RequirePatch {

  private var requireConfig: Option[String] = None

  private case class WebJar(name: String, version: String) {
    def this(p: (String, String)) = this(p._1, p._2)
  }

  def setupJavaScript(webjarUrlPrefix: String): String = this.synchronized{ requireConfig match {
    case Some(config) => config
    case _ =>
        val webjars = new WebJarAssetLocator().getWebJars
        val webjarsVersionsString = new StringBuilder()
        val webjarConfigsString = new StringBuilder()

        webjars.iterator.map(new WebJar(_)).foreach { info =>
          // assemble the webjar versions string
          webjarsVersionsString.append("'").append(info.name).append("': '").append(info.version).append("', ")
          // assemble the webjar config string
          webjarConfigsString.append("\n").append(getWebJarConfig(info));
        }
        // remove the trailing ", "
        if(webjarsVersionsString.length > 1)
          webjarsVersionsString.delete(webjarsVersionsString.length - 2, webjarsVersionsString.length)

        // assemble the JavaScript
        val javaScript = s"""
          |var webjars = {
          |    versions: { ${webjarsVersionsString.toString()}  },
          |    path: function(webjarid, path) {
          |        return $webjarUrlPrefix + webjarid + '/' + webjars.versions[webjarid] + '/' + path;
          |    }
          |};
          |
          |var previousRequireCallback;
          |if(require && require.callback) {
          |    previousRequireCallback = require.callback;
          |}
          |var require = {
          |    callback: function() {
          |        if(previousRequireCallback) previousRequireCallback();
          |        // no-op webjars requirejs plugin loader for backwards compatibility
          |        define('webjars', function () {
          |            return { load: function (name, req, onload, config) { onload(); } }
          |        });
          |
          |        // all of the webjar configs from their webjars-requirejs.js files
          |        ${webjarConfigsString.toString()}
          |    }
          |}
        """.stripMargin
        requireConfig = Some(javaScript)
        javaScript
  }}

  def getWebJarConfig(webjar: WebJar) = {

    val filename = s"${WebJarAssetLocator.WEBJARS_PATH_PREFIX}/${webjar.name}/${webjar.version}/webjars-requirejs.js"
    val inputStream = Option(classOf[RequireJS].getClassLoader.getResourceAsStream(filename))
    inputStream.fold(""){inputStream =>

      val config = s"// webjar config for ${webjar.name} \n" +
        withSource(Source.fromInputStream(inputStream)) { s =>
            s.getLines().mkString("\n")
        }

      config
    }
  }

  def withSource[T, S <: Source](source: S)(f: S => T): T = {
    val value = f(source)
    source.close()
    value
  }

}
