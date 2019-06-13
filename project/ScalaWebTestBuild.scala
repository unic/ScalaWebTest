import sbt._

import scala.xml.{Elem, NodeSeq}

object ScalaWebTestBuild {
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
