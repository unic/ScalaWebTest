/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalawebtest.core.browser

import java.net.URL

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.{Capabilities, WebDriver}
import org.scalawebtest.core.IntegrationSpec
import org.scalawebtest.core.configuration._


trait SeleniumChrome {
  self: IntegrationSpec =>

  override val loginConfig = new LoginConfiguration with SeleniumChromeConfiguration

  override val config = new Configuration with SeleniumChromeConfiguration

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

  trait SeleniumChromeConfiguration extends BaseConfiguration with WebDriverName with SeleniumBrowserConfiguration {
    override val webDriverName: String = classOf[SeleniumChrome].getCanonicalName
  }
}



