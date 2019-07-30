#
# ScalaWebTest Multistage build with precompiled tests and using the ScalaTest Runner http://www.scalatest.org/user_guide/using_the_runner
#

# Pull base image
FROM scalawebtest/sbt:3.0.1 AS builder

COPY --chown=sbtuser:sbtuser src /home/sbtuser/src

# Package tests
RUN sbt universal:packageZipTarball
# Unzip tests
RUN tar -xvzf /home/sbtuser/target/universal/tests-0.1.0-SNAPSHOT.tgz

# Start next stage with zulu-openjdk-alpine because of size
FROM azul/zulu-openjdk-alpine:11

RUN apk add --no-cache bash

# Add and use user swt
RUN addgroup --g 1001 swt && adduser -s /bin/bash --u 1001 -D -G swt swt
USER swt

# Copy tests
COPY --from=builder --chown=swt:swt  /home/sbtuser/tests-0.1.0-SNAPSHOT /home/swt

# Define working directory
WORKDIR /home/swt

CMD ["/home/swt/bin/tests", "-R", "/home/swt/lib/tests.tests-0.1.0-SNAPSHOT.jar", "-o"]
