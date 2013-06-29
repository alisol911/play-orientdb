import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-orientdb"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.orientechnologies" % "orient-commons" % "1.4.1",
    "com.orientechnologies" % "orientdb-core" % "1.4.1",
    "com.orientechnologies" % "orientdb-client" % "1.4.1",
    "com.orientechnologies" % "orientdb-object" % "1.4.1",
    "com.orientechnologies" % "orientdb-enterprise" % "1.4.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Orient Technologies Maven2 Repository" at "http://www.orientechnologies.com/listing/m2"
  )
  
}
