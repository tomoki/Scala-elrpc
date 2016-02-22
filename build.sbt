name := "Scala ELRPC"
libraryDependencies += "org.scalatest"  %% "scalatest"  % "2.2.4" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
libraryDependencies += "com.typesafe.akka" %% "akka-actor"  % "2.4.2"
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.2"

scalaVersion := "2.11.7"
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard"
)
