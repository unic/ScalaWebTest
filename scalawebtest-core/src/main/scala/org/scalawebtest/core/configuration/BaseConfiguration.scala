/*
 * Copyright 2016 the original author or authors.
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
package org.scalawebtest.core.configuration

import org.openqa.selenium.WebDriver
import org.scalawebtest.core.IntegrationSpec

abstract class BaseConfiguration() {
  var configurations: Map[String, WebDriver => Unit] = Map()

  def enableJavaScript(throwOnError: Boolean): Unit

  def disableJavaScript(): Unit

  def throwOnJavaScriptError(): Unit

  def swallowJavaScriptErrors(): Unit

  def enableCss(): Unit

  def disableCss(): Unit

  def unimplementedConfiguration(webDriverName: String): Unit = throw new RuntimeException(s"This is not configurable when working with $webDriverName. Please choose a different browser/webDriver.")
}

abstract class LoginConfiguration extends BaseConfiguration

abstract class Configuration extends BaseConfiguration {
  //initialize with sensible default configuration
  var navigateToBeforeEachEnabled = true
  var reloadOnNavigateToEnforced = false

  /**
    * Enabling navigateTo is the default. [[org.scalatest.BeforeAndAfterEach.beforeEach]] Test navigateTo is called with the current value of [[IntegrationSpec.path]]
    */
  def enableNavigateToBeforeEach(): Unit = navigateToBeforeEachEnabled = true

  /**
    * Disables the navigateTo call, which otherwise happens in [[org.scalatest.BeforeAndAfterEach.beforeEach]]
    */
  def disableNavigateToBeforeEach(): Unit = navigateToBeforeEachEnabled = false

  /**
    * By default navigateTo only navigates to the configured path, if it isn't the same as the currentPage.
    * By enabling enforceNavigateTo, [[org.scalatest.selenium.WebBrowser.goTo()]] is always called.
    */
  def enforceReloadOnNavigateTo(): Unit = reloadOnNavigateToEnforced = true

  /**
    * This is the default behavior. NavigateTo only calls [[org.scalatest.selenium.WebBrowser.goTo()]], if the
    * currentPage isn't the same as the configured path.
    */
  def doNotEnforceReloadOnNavigateTo(): Unit = reloadOnNavigateToEnforced = false
}
