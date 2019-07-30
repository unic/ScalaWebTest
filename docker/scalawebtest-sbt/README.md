# ScalaWebTest
This docker image builds and executes your [ScalaWebTest](https://scalawebtest) specifications.

## How to use
Your tests/specifications have to be stored in /home/sbtuser/src/test/scala. Either provide them via COPY command in your own Dockerfile, or use a volume mount.

**Windows**
`docker run -v %cd%\src\test\scala:/home/sbtuser/src/test/scala scalawebtest/sbt:3.0.1`

**Linux**
`docker run -v `pwd`/src/test/scala:/home/sbtuser/src/test/scala scalawebtest/sbt:3.0.1`

**docker-compose**
```yaml
version: '3'
services:
  scalawebtest:
    image: "scalawebtest/sbt:3.0.1"
    volumes:
      - ./src/test/scala:/home/sbtuser/src/test/scala
```

For additional examples and use-cases checkout [ScalaWebTest Examples](https://github.com/unic/ScalaWebTest/tree/master/examples)