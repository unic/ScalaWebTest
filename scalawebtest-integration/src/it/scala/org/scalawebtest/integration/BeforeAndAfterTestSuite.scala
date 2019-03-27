package org.scalawebtest.integration
import java.io.File

import org.openqa.selenium.chrome.ChromeDriverService

object BeforeAndAfterTestSuite {
  //before test suite
  System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe")

  val service = new ChromeDriverService.Builder()
    .usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe"))
    .usingAnyFreePort.build

  service.start()
  println("Started WireMock Server and tampered camel configuration and mock data")

  //after test suite
  sys addShutdownHook {

    service.stop()
    println("Stopped WireMock Server and reverted camel configuration")
  }

}
