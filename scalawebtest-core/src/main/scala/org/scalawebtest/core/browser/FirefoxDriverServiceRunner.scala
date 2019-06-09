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

import org.openqa.selenium.firefox.GeckoDriverService
import org.scalatest.ConfigMap
import org.scalawebtest.core.Configurable

object FirefoxDriverServiceRunner extends Configurable {

  //before test suite
  private val driverProperty = "webdriver.gecko.driver"

  var internalService: GeckoDriverService = _

  def assertInitialized(configMap: ConfigMap): GeckoDriverService = {
    val geckoDriverPath = requiredConfigFor[String](configMap)(driverProperty)
    internalService = new GeckoDriverService.Builder().usingDriverExecutable(new File(geckoDriverPath)).build()
    internalService
  }

  //after test suite
  sys addShutdownHook {
    internalService.stop()
    println("Stopped GeckoDriverService (the webdriver service used to control Firefox)")
  }

}
