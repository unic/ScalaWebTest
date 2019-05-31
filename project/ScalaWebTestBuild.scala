import sbt._

import scala.xml.{Elem, NodeSeq}

object ScalaWebTestBuild {
  def playJsonDependency(scope: Option[String])(scalaVersion: String): Seq[ModuleID] = {
    def playJsonDependency(playJsonVersion: String) = scope match {
      case None => Seq("com.typesafe.play" %% "play-json" % playJsonVersion)
      case Some(s) => Seq("com.typesafe.play" %% "play-json" % playJsonVersion % s)
    }

    def playJsonDependency213(playJsonVersion: String) = scope match {
      case None => Seq("com.typesafe.play" % "play-json_2.13.0-RC2" % playJsonVersion)
      case Some(s) => Seq("com.typesafe.play" % "play-json_2.13.0-RC2" % playJsonVersion % s)
    }

    scalaVersion match {
      case "2.11.12" => playJsonDependency("2.7.0")
      case "2.12.8" => playJsonDependency("2.8.0-M1")
      case "2.13.0-RC3" => playJsonDependency213("2.8.0-M1")
      case _ => Seq()
    }
  }

  def bomDependencies(versions: Map[String, String])(scalaVersion: String): Elem = {
    val scalaMajorVersion = scalaVersion.substring(0, "2.XX".length)
    val dependencies = <dependencyManagement>
      <dependencyManagementDependencies>
        <dependency>
          <groupId>org.scalawebtest</groupId>
          <artifactId>scalawebtest-core_{scalaMajorVersion}</artifactId>
          <version>{versions("scalaWebTest")}</version>
        </dependency>
        <dependency>
          <groupId>org.scalatest</groupId>
          <artifactId>scalatest_{scalaMajorVersion}</artifactId>
          <version>{versions("scalaTest")}</version>
        </dependency>
        <dependency>
          <groupId>org.seleniumhq.selenium</groupId>
          <artifactId>selenium-java</artifactId>
          <version>{versions("selenium")}</version>
        </dependency>
        <dependency>
          <groupId>org.seleniumhq.selenium</groupId>
          <artifactId>htmlunit-driver</artifactId>
          <version>{versions("htmlUnit")}</version>
        </dependency>
      </dependencyManagementDependencies>
    </dependencyManagement>
    dependencies
  }

  def scalaWebTestPomExtra: NodeSeq = {
      <url>http://www.scalawebtest.org</url>
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
      </developers>
  }
}
