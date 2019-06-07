#
# ScalaWebTest Multistage build with precompiled tests and using the ScalaTest Runner http://www.scalatest.org/user_guide/using_the_runner
#

# Pull base image
FROM scalawebtest/sbt:3.0.0-RC1 AS builder

COPY --chown=sbtuser:sbtuser src /home/sbtuser/src

RUN sbt universal:packageZipTarball
RUN tar -xvzf /home/sbtuser/target/universal/tests-0.1.0-SNAPSHOT.tgz

FROM azul/zulu-openjdk-alpine:11

RUN apk add --no-cache bash
COPY --from=builder /home/sbtuser/tests-0.1.0-SNAPSHOT /swt

CMD ["/swt/bin/tests", "-R", "/swt/lib/tests.tests-0.1.0-SNAPSHOT.jar", "-o"]
