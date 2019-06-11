name := """play-scala-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .configs(ITest)
  .settings( inConfig(ITest)(Defaults.testSettings) : _*)

lazy val ITest = config("it") extend(Test)

scalaSource in ITest := baseDirectory.value / "/it"

//Avoid ScalaDoc errors
sources in doc in Compile := List() 

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.199"
libraryDependencies += "org.scalawebtest" %% "scalawebtest-core" % "3.0.0-RC2" % "it"
libraryDependencies += "org.scalawebtest" %% "scalawebtest-json" % "3.0.0-RC2" % "it"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
