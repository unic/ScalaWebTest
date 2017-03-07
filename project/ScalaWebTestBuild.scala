import sbt.Keys.{scalaVersion, unmanagedSourceDirectories}
import sbt._

import scala.xml.{Elem, NodeSeq}

object ScalaWebTestBuild {
  def playJsonDependency(scope: Option[String])(scalaVersion: String): Seq[ModuleID] = scalaVersion match {
    case "2.11.8" => scope match {
      case None => Seq("com.typesafe.play" % "play-json_2.11" % "2.5.9")
      case Some(s) => Seq("com.typesafe.play" % "play-json_2.11" % "2.5.9" % s)
    }
    case "2.12.1" => scope match {
      case None => Seq("com.typesafe.play" % "play-json_2.12" % "2.6.0-M4")
      case Some(s) => Seq("com.typesafe.play" % "play-json_2.12" % "2.6.0-M4" % s)
    }
    case _ => Seq()
  }

  def crossVersionSharedSources(configurations: Seq[Configuration]): Seq[Setting[_]] =
    configurations.map { sc =>
      (unmanagedSourceDirectories in sc) ++= {
        (unmanagedSourceDirectories in sc).value.map { dir: File =>
          CrossVersion.partialVersion(scalaVersion.value) match {
            case Some((2, y)) if y == 10 => new File(dir.getPath + "_2.10")
            case Some((2, y)) if y >= 11 => new File(dir.getPath + "_2.11+")
          }
        }
      }
    }

  def bomDependencies(scalaVersion: String): Elem = {
    val scalaMajorVersion = scalaVersion.substring(0, "2.XX".length)
    val dependencies = <dependencyManagement>
      <dependencyManagementDependencies>
        <dependency>
          <groupId>org.scalatest</groupId>
          <artifactId>scalatest_
            {scalaMajorVersion}
          </artifactId>
          <version>3.0.1</version>
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
      </dependencyManagementDependencies>
    </dependencyManagement>
    dependencies
  }

  def scalaWebTestPomExtra: NodeSeq = {
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
      </developers>
  }
}