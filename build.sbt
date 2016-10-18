import com.typesafe.sbt.packager.docker._
import NativePackagerHelper._

name := "LunarRover"

version := "1.0.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "org.javassist" % "javassist" % "3.17.1-GA",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3",
  "net.jpountz.lz4" % "lz4" % "1.3.0",
  "com.typesafe" % "config" % "1.3.0"
)

resolvers := Seq(Resolver.defaultLocal,"handuser" at "http://sbt.handuser.com/maven2/")++resolvers.value

val root = (project in file(".")).enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("main.Main")