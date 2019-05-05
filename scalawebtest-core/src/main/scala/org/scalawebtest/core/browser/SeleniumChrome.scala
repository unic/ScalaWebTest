package org.scalawebtest.core.browser

import java.net.URL

import org.openqa.selenium.{Capabilities, WebDriver}
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.scalawebtest.core.IntegrationSpec


trait SeleniumChrome {
  self: IntegrationSpec =>

  //this imports the ChromeDriverServiceRunner object, and therefore allows it to execute commands before and after the test suite was run
  private val driverServiceRunner = ChromeDriverServiceRunner

  override implicit val webDriver: WebDriver = new ChromeRemoteWebDriver(
    driverServiceRunner.service.getUrl,
    new ChromeOptions().addArguments("--no-sandbox", "--headless")
  )

  class ChromeRemoteWebDriver(remoteAddress: URL , capabilities: Capabilities ) extends RemoteWebDriver(remoteAddress, capabilities) {
    override def getPageSource: String = {
      super.getPageSource
        .replaceFirst("""<html xmlns="http://www.w3.org/1999/xhtml"><head></head><body><pre style="word-wrap: break-word; white-space: pre-wrap;">""", "")
        .replaceFirst("""</pre></body></html>""", "")
    }
  }

}



