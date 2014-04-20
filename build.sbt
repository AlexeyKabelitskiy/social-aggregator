import play.Project._

name := "social-aggregator"

version := "1.0"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "jquery" % "2.1.0-2",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "underscorejs" % "1.6.0-1",
  "org.webjars" % "backbonejs" % "1.1.2-1"
)

playScalaSettings

requireJs += "home.js"

requireJsShim += "home.js"