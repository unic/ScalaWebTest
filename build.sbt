lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := "1.0.1",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-deprecation")
)

lazy val core = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.0",
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
      "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0",
      "org.slf4j" % "slf4j-api" % "1.7.20"
    )
  )

lazy val aem = project
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val integration_test = project
  .configs(IntegrationTest)
  .settings(commonSettings: _*)
  .settings(Defaults.itSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.0" % "it",
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "it",
      "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0" % "it",
      "org.slf4j" % "slf4j-api" % "1.7.20" % "it",
      "com.typesafe.play" % "play-json_2.11" % "2.5.4" % "it"
    )
  )
  .enablePlugins(JettyPlugin)
  .dependsOn(core)
  .dependsOn(aem)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
