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

import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions, FirefoxProfile, GeckoDriverService}
import org.scalatest.ConfigMap
import org.scalawebtest.core.configuration._
import org.scalawebtest.core.{Configurable, IntegrationSpec}

import scala.jdk.CollectionConverters._


trait SeleniumFirefox extends Configurable {
  self: IntegrationSpec =>

  override val loginConfig = new LoginConfiguration with SeleniumFirefoxConfiguration

  override val config = new Configuration with SeleniumFirefoxConfiguration

  //this imports the FirefoxDriverServiceRunner object, and therefore allows it to execute commands before and after the test suite was run
  private val driverServiceRunner = FirefoxDriverServiceRunner

  override def prepareWebDriver(configMap: ConfigMap): Unit = {
    val driverService = driverServiceRunner.assertInitialized(configMap)

    val firefoxArguments =
      configFor[String](configMap)("webdriver.firefox.arguments").map(_.split(',').toList)
        .getOrElse(List("--headless", "--no-remote"))
        .asJava

    val profile = new FirefoxProfile()
    profile.setPreference("devtools.jsonview.enabled", false)

    webDriver = new CleanedPageSourceFirefoxDriver(
      driverService,
      new FirefoxOptions()
        .addArguments(firefoxArguments)
        .setProfile(profile)
    )
  }

  class CleanedPageSourceFirefoxDriver(driverService: GeckoDriverService, firefoxOptions: FirefoxOptions) extends FirefoxDriver(driverService, firefoxOptions) {
    override def getPageSource: String = {
      super.getPageSource
        .replaceFirst("""<html.*><head.*><link.*></head><body><pre>""", "")
        .replaceFirst("""</pre></body></html>""", "")
    }
  }

  trait SeleniumFirefoxConfiguration extends BaseConfiguration with WebDriverName with SeleniumBrowserConfiguration {
    override val webDriverName: String = classOf[SeleniumFirefox].getCanonicalName
  }

}



