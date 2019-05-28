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

import java.io.File
import java.net.URL

import org.openqa.selenium.chrome.ChromeDriverService
import org.scalawebtest.core.Configurable

object ChromeDriverServiceRunner extends Configurable {
  //before test suite
  private val driverServiceUrlProperty = "WEBDRIVER_CHROME_DRIVER_SERVICE_URL"
  private val driverProperty = "webdriver.chrome.driver"

  private val runningChromeDriverServicePort = configFor[URL](driverServiceUrlProperty)

  var serviceOrPort: Either[ChromeDriverService, URL] = runningChromeDriverServicePort match {
    case Some(url) =>
      println(s"Not taking any action regarding ChromeDriverService, because it is managed outside of ScalaWebTest and access was provided via $driverServiceUrlProperty property.")
      Right(url)
    case None =>
      val chromeDriverPath = requiredConfigFor[String](driverProperty)

      val service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File(chromeDriverPath))
        .usingAnyFreePort.build

      service.start()
      println(s"Started ChromeDriverService from path $chromeDriverPath")
      Left(service)
  }

  def url: URL = serviceOrPort match {
    case Left(s) => s.getUrl
    case Right(u) => u
  }

  //after test suite
  sys addShutdownHook {
    serviceOrPort match {
      case Left(s) =>
        s.stop()
        println("Stopped ChromeDriverService")
      case Right(p) =>
        println(s"Not taking any action regarding ChromeDriverService, because it is managed outside of ScalaWebTest and access was provided via $driverServiceUrlProperty property.")
    }
  }
}

