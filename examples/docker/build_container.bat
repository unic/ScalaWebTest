cd C:\Users\daniel.rey\git\ScalaWebTest\examples\docker
docker run -it --rm -v %CD%\src\test\scala:/home/sbtuser/src/test/scala scalawebtest/sbt:3.0.0
