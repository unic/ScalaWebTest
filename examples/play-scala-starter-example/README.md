# Test your Play Application with ScalaWebTest
This examples shows how you can test your [play framework](https://www.playframework.com/) application with ScalaWebTest.

The `build.sbt` is a modified version of  
https://github.com/playframework/play-samples/tree/2.8.x/play-scala-starter-example/build.sbt.
Additional there is a folder `it`, with a few simple tests. 

To run this example, clone the [play-samples](https://github.com/playframework/play-samples.git) repository and copy `build.sbt` and `it` into the `play-scala-starter-example` folder.

Last but not least, the `ci-build.sh` script illustrates, which tasks you have to execute on your build server, 
to run integration tests against your play application.
