import ScalaWebTestBuild._

import scala.xml.transform.RewriteRule

lazy val supportedScalaVersions = Seq("3.0.2", "2.13.6")

val projectVersion = "4.0.1-SNAPSHOT"
val scalaTestVersion = "3.2.9"
val scalaTestSeleniumVersion = "3.2.9.0"
val seleniumVersion = "3.141.59"
val htmlUnitVersion = "2.53.0"
val slf4jVersion = "1.7.32"
val playJsonVersion = "2.10.0-RC5"

val versions = Map("scalaWebTest" -> projectVersion, "scalaTest" -> scalaTestVersion, "selenium" -> seleniumVersion, "htmlUnit" -> htmlUnitVersion, "playJson" -> playJsonVersion)

val scalaWebTestSeries = "4.0.0"

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false)
  .settings(crossScalaVersions := Nil)
  .settings(mimaFailOnNoPrevious := false)
  .aggregate(core, aem, json, bom, integration_test)

lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := projectVersion,
  scalaVersion := "3.0.2",
  scalacOptions := Seq("-unchecked", "-deprecation", "-Xfatal-warnings", "-rewrite", "-source:3.0-migration"),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ => false },
  dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.13.0",
  pomExtra := scalaWebTestPomExtra,
  mimaPreviousArtifacts := Set(organization.value %% moduleName.value % scalaWebTestSeries)
)

lazy val core = Project(id = "scalawebtest-core", base = file("scalawebtest-core"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion,
      "org.scalatestplus" %% "selenium-3-141" % scalaTestSeleniumVersion,
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion,
      "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitVersion,
      "org.jsoup" % "jsoup" % "1.14.3",
      "org.slf4j" % "slf4j-api" % slf4jVersion,
    )
  )

lazy val aem = Project(id = "scalawebtest-aem", base = file("scalawebtest-aem"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % playJsonVersion)
  .dependsOn(core)

lazy val json = Project(id = "scalawebtest-json", base = file("scalawebtest-json"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % playJsonVersion)
  .dependsOn(core)

lazy val bom = Project(id = "scalawebtest-bom", base = file("scalawebtest-bom"))
  .settings(description := "ScalaWebTest (Bill of Materials)")
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(
    Compile / packageBin / publishArtifact := false,
    Compile / packageDoc / publishArtifact := false,
    Compile / packageSrc / publishArtifact := false,
  )
  .settings(
    pomExtra := pomExtra.value ++ scalaVersion(bomDependencies(versions)).value
  )
  .settings(pomPostProcess := { (node: scala.xml.Node) =>
    val rewriteRule: RewriteRule =
      new scala.xml.transform.RewriteRule {
        override def transform(n: scala.xml.Node): scala.xml.NodeSeq = {
          val name = n.nameToString(new StringBuilder).toString
          if (name == "dependencies") {
            scala.xml.NodeSeq.Empty
          }
          else if (name == "dependencyManagementDependencies") {
            <dependencies>
              {n.child}
            </dependencies>
          }
          else {
            n
          }
        }
      }
    val transformer = new scala.xml.transform.RuleTransformer(rewriteRule)
    transformer.transform(node).head
  })
  .settings(mimaFailOnNoPrevious := false)

lazy val integration_test = Project(id = "scalawebtest-integration", base = file("scalawebtest-integration"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(
    libraryDependencies ++= Seq(
      "javax.servlet" % "javax.servlet-api" % "4.0.1" % "provided",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "it",
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % "it",
      "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitVersion % "it",
      "org.slf4j" % "slf4j-api" % slf4jVersion % "it",
      "org.slf4j" % "slf4j-simple" % slf4jVersion % "it",
      "com.typesafe.play" %% "play-json" % playJsonVersion % "it",
    )
  )
  .settings(mimaFailOnNoPrevious := false)
  .enablePlugins(JettyPlugin)
  .settings(Jetty / containerPort := 9090)
  .dependsOn(core)
  .dependsOn(aem)
  .dependsOn(json)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
