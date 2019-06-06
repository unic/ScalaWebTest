#!/bin/sh
sbt clean compile test stage
target/universal/stage/bin/play-scala-starter-example -Dplay.http.secret.key='ScalaWebTest- integration testing made easy' &
sbt it:test
kill -15 `cat target/universal/stage/RUNNING_PID`