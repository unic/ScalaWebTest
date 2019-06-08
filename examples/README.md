# Examples
The main area to look for good examples on how to use ScalaWebTest is and remains the scalawebtest-integration module.
It contains many integration tests, which are used to verify ScalaWebTests own behavior, therefore they cover most of its
functionality. Nevertheless, some features and use cases can not be part of our integration tests, because they would
add additional dependencies and complexity.

## Integration with play framework
The sub-folder *play-scala-starter-example* contains a modified version of the `build.sbt` from 
https://github.com/playframework/play-samples/tree/2.8.x/play-scala-starter-example.
Additional there is a folder `it`, with a few simple tests. 
To test it, clone the `play-samples` repository and copy `build.sbt` and `it` into the `play-scala-starter-example` folder.

Last but not least, the `ci-build.sh` script illustrates, which tasks you have to execute on your build server, 
to run integration tests against your play application.

## Dockerize building and running the tests
The sub-folder *docker* contains examples to build and run your tests with a docker image provided by ScalaWebTest. 
It also shows how to build a small docker container, with pre-compiled tests in it.
