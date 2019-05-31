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
package org.scalawebtest.core.configuration

import org.slf4j.{Logger, LoggerFactory}

trait WebDriverName {
  val webDriverName: String
}

trait SeleniumBrowserConfiguration {
  self: BaseConfiguration with WebDriverName =>

  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  override def enableJavaScript(throwOnError: Boolean): Unit = logger.warn(s"JavaScript is always active with $webDriverName. This configuration is superfluous.")

  override def disableJavaScript(): Unit = unimplementedConfiguration(webDriverName)

  override def throwOnJavaScriptError(): Unit = unimplementedConfiguration(webDriverName)

  override def swallowJavaScriptErrors(): Unit = logger.warn(s"JavaScript errors will always get swallowed working with $webDriverName. This configuration is superfluous.")

  override def enableCss(): Unit = logger.warn(s"CSS interpretation is always active when working with $webDriverName. This configuration is superfluous.")

  override def disableCss(): Unit = unimplementedConfiguration(webDriverName)
}
