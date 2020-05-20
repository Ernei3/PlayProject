name := "projektPlay"
 
version := "1.0" 
      
lazy val `projektplay` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

resolvers += Resolver.jcenterRepo

scalaVersion := "2.12.8"

libraryDependencies ++= Seq( ehcache , ws , specs2 % Test , guice )
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.3"
)
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.21.0"


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      