lazy val commonSettings = Seq(
  organization := "org.scalawebtest",
  version := "1.0.4-SNAPSHOT",
  scalaVersion := "2.11.8",
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
  pomExtra := (
    <url>http://www.scalatest.org</url>
      <licenses>
        <license>
          <name>The Apache Software License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        </license>
      </licenses>
      <scm>
        <url>git@github.com/unic/ScalaWebTest.git</url>
        <connection>scm:git:git@github.com/unic/ScalaWebTest.git</connection>
      </scm>
      <developers>
        <developer>
          <id>DaniRey</id>
          <name>Daniel Rey</name>
          <email>daniel.rey@unic.com</email>
          <organization>Unic AG</organization>
          <organizationUrl>http://www.unic.com</organizationUrl>
        </developer>
        <developer>
          <id>thedodobird2</id>
          <name>Hudson Muff</name>
          <email>hudson.muff@unic.com</email>
          <organization>Unic AG</organization>
          <organizationUrl>http://www.unic.com</organizationUrl>
        </developer>
      </developers>)
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
  pomExtra := pomExtra.value ++ (
        <dependencyManagement>
          <dependencyManagementDependencies>
            <dependency>
              <groupId>org.scalatest</groupId>
              <artifactId>scalatest_2.11</artifactId>
              <version>3.0.0</version>
            </dependency>
            <dependency>
              <groupId>org.seleniumhq.selenium</groupId>
              <artifactId>selenium-java</artifactId>
              <version>2.53.1</version>
            </dependency>
            <dependency>
              <groupId>org.seleniumhq.selenium</groupId>
              <artifactId>selenium-htmlunit-driver</artifactId>
              <version>2.52.0</version>
            </dependency>
            <dependency>
              <groupId>org.slf4j</groupId>
              <artifactId>slf4j-api</artifactId>
              <version>1.7.20</version>
            </dependency>
          </dependencyManagementDependencies>
        </dependencyManagement>)
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
            <dependencies>{n.child}</dependencies>
          }
          else {
            n
          }
        }
      }
    val transformer = new scala.xml.transform.RuleTransformer(rewriteRule)
    transformer.transform(node)(0)
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
      "org.slf4j" % "slf4j-api" % "1.7.20" % "it",
      "com.typesafe.play" % "play-json_2.11" % "2.5.4" % "it"
    )
  )
  .enablePlugins(JettyPlugin)
  .dependsOn(core)
  .dependsOn(aem)

addCommandAlias("inttest", "; jetty:start ; it:test ; jetty:stop")
