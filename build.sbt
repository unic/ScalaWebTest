import ScalaWebTestBuild._

import scala.xml.transform.RewriteRule

lazy val supportedScalaVersions = Seq("2.13.0", "2.12.8", "2.11.12")

val projectVersion = "3.0.0-SNAPSHOT"
val scalaTestVersion = "3.0.8"
val seleniumVersion = "3.141.59"
val htmlUnitVersion = "2.35.1"
val slf4jVersion = "1.7.26"
val playJsonVersion = "2.7.4"

val versions = Map("scalaWebTest" -> projectVersion, "scalaTest" -> scalaTestVersion, "selenium" -> seleniumVersion, "htmlUnit" -> htmlUnitVersion)

val scalaWebTestSeries = "3.0.0"
def mimaSettings(projectName: String) = Seq(
   mimaPreviousArtifacts := Set("org.scalawebtest" %% projectName % scalaWebTestSeries)
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false)
  .settings(crossScalaVersions := Nil)
  .aggregate(core, aem, json, bom, integration_test)

lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := projectVersion,
  scalaVersion := "2.12.8",
  scalacOptions := Seq("-unchecked", "-deprecation"),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ => false },
  pomExtra := scalaWebTestPomExtra
)

lazy val core = Project(id = "scalawebtest-core", base = file("scalawebtest-core"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion,
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion,
      "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitVersion,
      "org.jsoup" % "jsoup" % "1.11.3",
      "org.slf4j" % "slf4j-api" % slf4jVersion
    )
  )
  .settings(mimaSettings("scalawebtest-core"))

lazy val aem = Project(id = "scalawebtest-aem", base = file("scalawebtest-aem"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % playJsonVersion)
  .settings(mimaSettings("scalawebtest-aem"))
  .dependsOn(core)

lazy val json = Project(id = "scalawebtest-json", base = file("scalawebtest-json"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % playJsonVersion)
  .settings(mimaSettings("scalawebtest-json"))
  .dependsOn(core)

lazy val bom = Project(id = "scalawebtest-bom", base = file("scalawebtest-bom"))
  .settings(description := "ScalaWebTest (Bill of Materials)")
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(
    publishArtifact in(Compile, packageBin) := false,
    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in(Compile, packageSrc) := false)
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
      "com.typesafe.play" %% "play-json" % playJsonVersion % "it"
    )
  )
  .enablePlugins(JettyPlugin)
  .settings(containerPort in Jetty := 9090)
  .dependsOn(core)
  .dependsOn(aem)
  .dependsOn(json)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
