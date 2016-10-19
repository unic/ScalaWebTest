# ScalaWebTest [![Build Status](https://travis-ci.org/unic/ScalaWebTest.svg?branch=master)](https://travis-ci.org/unic/ScalaWebTest)
ScalaWebTest is a library for writing ScalaTest/Selenium based integration tests for websites. It helps you with your basic setup and provides a new and very efficient approach to testing.

In manufacturing it is common to use gauges (also called checking gauges or testing gauges) to verify whether a workpiece meets predefined criteria and tolerances. ScalaWebTest transfers this concept to the world of web integration testing. You can define a gauge in HTML, and use it to verify your web application. This greatly improves the readability, simplicity of your integration tests.

Read the full documentation on our website https://unic.github.io/ScalaWebTest/

# Adding  scalawebtest to your sbt project
libraryDependencies += "org.scalawebtest" % "core_2.11" % "1.0.0" % "it"

# Adding scalawebtest to your maven project
To add scalawebtest to your project you only need one dependency. 
<dependencies>
    <dependency>
        <groupId>org.scalawebtest</groupId>
        <artifactId>core_2.11/artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

AEM projects will benefit from using the aem module as well.
<dependencies>
    <dependency>
        <groupId>org.scalawebtest</groupId>
        <artifactId>core_2.11/artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.scalawebtest</groupId>
        <artifactId>aem_2.11/artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

If you want to make sure, that the important transitive dependencies of the scalawebtest project are correctly managed, 
you can user our bill of materials. This is recommended.

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.scalawebtest</groupId>
            <artifactId>bom_2.11</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>