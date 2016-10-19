# ScalaWebTest [![Build Status](https://travis-ci.org/unic/ScalaWebTest.svg?branch=master)](https://travis-ci.org/unic/ScalaWebTest)
ScalaWebTest is a library for writing ScalaTest/Selenium based integration tests for websites. It helps you with your basic setup and provides a new and very efficient approach to testing.

In manufacturing it is common to use gauges (also called checking gauges or testing gauges) to verify whether a workpiece meets predefined criteria and tolerances. ScalaWebTest transfers this concept to the world of web integration testing. You can define a gauge in HTML, and use it to verify your web application. This greatly improves the readability, simplicity of your integration tests.

Read the full documentation on our website https://unic.github.io/ScalaWebTest/

## Adding  scalawebtest to your sbt project
```
libraryDependencies += "org.scalawebtest" % "core_2.11" % "1.0.0" % "it"
```

## Adding scalawebtest to your maven project
To add scalawebtest to your project you only need one dependency.
```
<dependencies>
    <dependency>
        <groupId>org.scalawebtest</groupId>
        <artifactId>core_2.11/artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

AEM projects will benefit from using the aem module as well.
```
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
```

If you want to make sure, that the important transitive dependencies of the scalawebtest project are correctly managed, 
you can user our bill of materials. This is recommended.
```
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
```

## How to Release

The release process of ScalaWebTest is currently done manually. The process is done as follows:

1. Switch the version in `build.sbt` to that which is to be released (e.g. from `0.0.1-SNAPSHOT` to `1.0.0`)
2. Commit your changes with the commit message "Release x.x.x" (e.g. `Release 1.0.0`)
3. In your command line type in the following:
	1. the fast way:
		1. `sbt clean compile`
		2. `sbt publishLocal`
	2. the long way:
		1. `sbt` - enters the sbt interactive mode
		2. `clean` - deletes all generated files (target)
		3. `compile` - compiles the main sources
		4. `inttest` - compiles the integration test sources
		5. `publishLocal` - creates all of the below plus the poms
			1. `package` - creates jars
			2. `packageDoc` - creates javadoc jars
			3. `packageSrc` - creates source jars
		6. `exit` - exits the sbt interactive mode
4. On GitHub go to the releases page, click "Draft a new release", and type in the to-be-released version (e.g. `1.0.0`)
5. Add all the binaries that were created above for the core and aem modules (can be found in the respective target directory) and click "Publish release"
7. Switch the version in `build.sbt` to the snapshot version (e.g. from `1.0.0` to `1.0.0-SNAPSHOT`) and update the documentation if needed
8. Finally commit those changes with the commit message "Release x.x.x complete" (e.g. `Release 1.0.0 complete`)