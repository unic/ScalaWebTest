# Examples
The main area to look for good examples on how to use ScalaWebTest is and remains the scalawebtest-integration module.
It contains many integration tests, which are used to verify ScalaWebTests own behavior, therefore they cover most of its
functionality. Nevertheless, some features and use cases can not be part of our integration tests, because they would
add additional dependencies and complexity.

## Integration with play framework
The sub-folder *play-scala-starter-example* contains an example, which illustrates how to test a play framework application with ScalaWebTest. 

## Dockerize building and running the tests
The sub-folder *docker* contains examples to build and run your tests with a docker image provided by ScalaWebTest. 
It also shows how to build a small docker container, with pre-compiled tests in it.

## Educational examples for documentation
The sub-folder *documentation*, contains examples as used by the documentation. They have a more educational focus then
the integration tests of the scalawebtest-integration module.
