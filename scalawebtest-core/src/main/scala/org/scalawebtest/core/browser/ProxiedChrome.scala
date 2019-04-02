package org.scalawebtest.core.browser

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalawebtest.core.IntegrationSpec


trait ProxiedChrome { self: IntegrationSpec =>
  //this import the object, and therefore allows it to execute commands before and after the test suite was run
  private val driverServiceRunner =  ChromeDriverServiceRunner

  override implicit val webDriver: WebDriver = new RemoteWebDriver(driverServiceRunner.service.getUrl, new ChromeOptions().addArguments("--no-sandbox", "--headless"))
}



