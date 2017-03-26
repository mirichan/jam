organization := "ch.mirichan"
name := "jam-server"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.1"

val Http4sVersion = "0.15.7a"
val ArgonautVersion = "6.2-RC2"

libraryDependencies ++= Seq(
 "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
 "org.http4s"     %% "http4s-argonaut"     % Http4sVersion,
 "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
 "io.argonaut"    %% "argonaut"            % ArgonautVersion,
 "ch.qos.logback" %  "logback-classic"     % "1.2.1"
)
