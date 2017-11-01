import ScalaWebTestBuild._

crossScalaVersions := Seq("2.12.3", "2.11.11", "2.10.6")

val projectVersion = "2.0.1-SNAPSHOT"
val scalaTestVersion = "3.0.4"
val seleniumVersion = "3.6.0"
val htmlUnitVersion = "2.27"
val slf4jApiVersion = "1.7.25"

val versions = Map("scalaWebTest" -> projectVersion, "scalaTest" -> scalaTestVersion, "selenium" -> seleniumVersion, "htmlUnit" -> htmlUnitVersion)

val scalaWebTestSeries = "1.1.0"
def mimaSettings(projectName: String) = Seq(
   mimaPreviousArtifacts := Set("org.scalawebtest" %% projectName % scalaWebTestSeries)
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false)
  .aggregate(core, aem, json, bom, integration_test)

lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := projectVersion,
  scalaVersion := "2.12.3",
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
) ++ crossVersionSharedSources(Seq(Test, Compile))

lazy val core = Project(id = "scalawebtest-core", base = file("scalawebtest-core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion,
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion,
      "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitVersion,
      "org.slf4j" % "slf4j-api" % slf4jApiVersion
    )
  )
  .settings(mimaSettings("scalawebtest-core"))

lazy val aem = Project(id = "scalawebtest-aem", base = file("scalawebtest-aem"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= scalaVersion(playJsonDependency(scope = None)).value)
  .settings(mimaSettings("scalawebtest-aem"))
  .dependsOn(core)

lazy val json = Project(id = "scalawebtest-json", base = file("scalawebtest-json"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= scalaVersion(playJsonDependency(scope = None)).value)
  .settings(mimaSettings("scalawebtest-json"))
  .dependsOn(core)

lazy val bom = Project(id = "scalawebtest-bom", base = file("scalawebtest-bom"))
  .settings(description := "ScalaWebTest (Bill of Materials)")
  .settings(commonSettings: _*)
  .settings(
    publishArtifact in(Compile, packageBin) := false,
    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in(Compile, packageSrc) := false)
  .settings(
    pomExtra := pomExtra.value ++ scalaVersion(bomDependencies(versions)).value
  )
  .settings(pomPostProcess := { (node: scala.xml.Node) =>
    val rewriteRule =
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
  .settings(crossVersionSharedSources(Seq(IntegrationTest)): _*)
  .settings(
    libraryDependencies ++= Seq(
      "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "it",
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % "it",
      "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitVersion % "it",
      "org.slf4j" % "slf4j-api" % slf4jApiVersion % "it"
    )
  )
  .settings(libraryDependencies ++= scalaVersion(playJsonDependency(Some("it"))).value)
  .enablePlugins(JettyPlugin)
  .settings(containerPort in Jetty := 9090)
  .dependsOn(core)
  .dependsOn(aem)
  .dependsOn(json)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
