# Dockerize your integration tests
In a containerized environment it can make sense to run your integration tests from within a container for two reasons.

* The build tools for your app are containerized, your tests should not be different
* You want to access the internal networking of your container orchestration plattform

## Two container approach
To build your tests, we suggest to use the `scalawebtest/sbt` image. 
Usually it is best to fire it up and keep the `sbt` shell open while you develop the tests. 
Doing so allows `sbt` to incrementally compile your tests and your feedback loop should be faster.

You might use `docker-compose run scalawebtest sbt` to do so.

As part of a release, you can then build a container with your pre-compiled tests in it. Use the `Dockerfile` to do so.
The resulting container is fast and small.

You can use environment variables `SCALAWEBTEST_BASE_URI` and `SCALAWEBTEST_LOGIN_URI` to control `config.baseUri` and `loginConfig.loginUri` 

If you tag the container with the pre-compiled test as `swt` (building it using `docker build . -t swt`)
You can then run it with `docker run --rm -it swt`
To point it to a different server, use the environment variables like so `docker run --rm -it -e "SCALAWEBTEST_BASE_URI=https://www.unic.com" swt`

For additional features consult the [ScalaTest Runner documentation.](http://www.scalatest.org/user_guide/using_the_runner)
