ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "akka-web-cruler",
    idePackagePrefix := Some("com.krushna")
  )
mainClass := Some("com.krushna.MainApp")
lazy val akkaVersion = "2.6.19"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19",
  "com.typesafe.akka" %% "akka-cluster" % "2.6.19",
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.ning" % "async-http-client" % "1.9.40",
  "org.jsoup" % "jsoup" % "1.14.3",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
  "org.scalameta" %% "munit" % "0.7.29" % Test
)
