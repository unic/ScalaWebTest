object BeforeAndAfterAllTestSuites {

  println("This is printed before the first test is executed")

  sys addShutdownHook  {
    println("This is printed after the last test is executed")
  }
}
