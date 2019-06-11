ThisBuild / scalaVersion := "2.12.8"

lazy val tests = (project in file("."))
  .settings(
    name := "Documentation Examples",
    libraryDependencies += "org.scalawebtest" %% "scalawebtest-core" % "3.0.0-SNAPSHOT",
    libraryDependencies += "org.scalawebtest" %% "scalawebtest-json" % "3.0.0-SNAPSHOT",
    libraryDependencies += "org.scalawebtest" %% "scalawebtest-aem" % "3.0.0-SNAPSHOT",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.26",
  )
