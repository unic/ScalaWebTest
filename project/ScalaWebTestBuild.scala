import sbt.Keys.{scalaVersion, unmanagedSourceDirectories}
import sbt._

import scala.xml.{Elem, NodeSeq}

object ScalaWebTestBuild {
  def playJsonDependency(scope: Option[String])(scalaVersion: String): Seq[ModuleID] = {
    val playJsonVersion = "2.6.10"
    def playJsonDependency = scope match {
        case None => Seq("com.typesafe.play" %% "play-json" % playJsonVersion)
        case Some(s) => Seq("com.typesafe.play" %% "play-json" % playJsonVersion % s)
      }
    scalaVersion match {
      case "2.11.12" => playJsonDependency
      case "2.12.7" => playJsonDependency
      case _ => Seq()
    }
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
