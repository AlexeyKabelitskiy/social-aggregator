import play.Project._

name := "social-aggregator"

version := "1.0"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "jquery" % "2.1.0-2",
  "org.webjars" % "jquery-blockui" % "2.65",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "underscorejs" % "1.6.0-1",
  "org.webjars" % "backbonejs" % "1.1.2-1",
  "org.webjars" % "momentjs" % "2.6.0",
  "com.github.nscala-time" %% "nscala-time" % "1.0.0",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0"
)

playScalaSettings

requireJs += "home.js"

requireJsShim += "home.js"