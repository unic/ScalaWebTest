crossScalaVersions := Seq("2.12.1", "2.11.8", "2.10.6")

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false)
  .aggregate(core, aem, bom, integration_test)

lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := "1.0.5-SNAPSHOT",
  scalaVersion := "2.12.1",
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
      "org.scalatest" %% "scalatest" % "3.0.0",
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
      "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0",
      "org.slf4j" % "slf4j-api" % "1.7.20"
    )
  )

lazy val aem = Project(id = "scalawebtest-aem", base = file("scalawebtest-aem"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= scalaVersion(playJsonDependency(scope = None)).value)
  .dependsOn(core)

lazy val bom = Project(id = "scalawebtest-bom", base = file("scalawebtest-bom"))
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

lazy val integration_test = Project(id = "scalawebtest-integration", base = file("scalawebtest-integration"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(commonSettings: _*)
  .settings(crossVersionSharedSources(Seq(IntegrationTest)): _*)
  .settings(
    libraryDependencies ++= Seq(
      "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.0" % "it",
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "it",
      "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0" % "it",
      "org.slf4j" % "slf4j-api" % "1.7.20" % "it"
    )
  )
  .settings(libraryDependencies ++= scalaVersion(playJsonDependency(Some("it"))).value)
  .enablePlugins(JettyPlugin)
  .settings(containerPort in Jetty := 9090)
  .dependsOn(core)
  .dependsOn(aem)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
