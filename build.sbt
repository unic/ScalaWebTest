crossScalaVersions := Seq("2.12.0", "2.11.8", "2.10.6")

lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := "1.0.4-SNAPSHOT",
  scalaVersion := "2.12.0",
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

lazy val bom = project
  .settings(description := "ScalaWebTest (Bill of Materials)")
  .settings(commonSettings: _*)
  .settings(
    publishArtifact in(Compile, packageBin) := false,
    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in(Compile, packageSrc) := false)
  .settings(
    pomExtra := pomExtra.value ++ scalaVersion(bomDependencies).value
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
      "org.slf4j" % "slf4j-api" % "1.7.20" % "it"
    )
  )
  .settings(libraryDependencies ++= scalaVersion(playJsonDependency).value)
  .enablePlugins(JettyPlugin)
  .dependsOn(core)
  .dependsOn(aem)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
