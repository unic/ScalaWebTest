#
# ScalaWebTest SBT Dockerfile
#
# Pull base image
FROM hseeberger/scala-sbt:8u212_1.2.8_2.13.0

# Define working directory
WORKDIR /home/sbtuser

USER sbtuser

COPY --chown=sbtuser:sbtuser build.sbt /home/sbtuser/build.sbt
COPY --chown=sbtuser:sbtuser project/build.properties /home/sbtuser/project/build.properties
COPY --chown=sbtuser:sbtuser project/plugins.sbt /home/sbtuser/project/plugins.sbt

RUN \
  mkdir -p src/test/scala && \
  echo "case object Temp" > src/test/scala/Temp.scala && \
  sbt test:compile && \
  rm src/test/scala/Temp.scala

CMD ["sbt", "test"]
