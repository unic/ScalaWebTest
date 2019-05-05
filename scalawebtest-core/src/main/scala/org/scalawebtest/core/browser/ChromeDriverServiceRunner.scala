package org.scalawebtest.core.browser

import java.io.File

import org.openqa.selenium.chrome.ChromeDriverService
import org.scalawebtest.core.Configurable

object ChromeDriverServiceRunner extends Configurable {
  //before test suite
  private val chromeDriverPath = requiredConfigFor("webdriver.chrome.driver")

  val service: ChromeDriverService = new ChromeDriverService.Builder()
    .usingDriverExecutable(new File(chromeDriverPath))
    .usingAnyFreePort.build

  service.start()

  println(s"Started ChromeDriverService from path $chromeDriverPath")

  //after test suite
  sys addShutdownHook {
    service.stop()
    println("Stopped ChromeDriverService")
  }
}

