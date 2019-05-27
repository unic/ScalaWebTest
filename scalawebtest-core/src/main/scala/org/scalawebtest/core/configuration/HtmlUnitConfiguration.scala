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

import org.openqa.selenium.WebDriver
import org.scalawebtest.core.WebClientExposingDriver

trait HtmlUnitConfiguration {
  self: BaseConfiguration =>
  //initialize with sensible default configuration
  disableJavaScript()
  swallowJavaScriptErrors()
  disableCss()

  /**
    * Enable JavaScript evaluation in the webDriver
    * and choose whether to throw on JavaScript error
    */
  override def enableJavaScript(throwOnError: Boolean): Unit = {
    configurations += "enableJavaScript" ->
      ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setJavaScriptEnabled(true))
    configurations += "throwOnJSError" ->
      ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setThrowExceptionOnScriptError(throwOnError))
  }

  /**
    * Disable JavaScript evaluation as well as throwing on JavaScript error in the webDriver
    */
  override def disableJavaScript(): Unit = {
    configurations += "enableJavaScript" -> ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setJavaScriptEnabled(false))
    configurations += "throwOnJSError" -> ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setThrowExceptionOnScriptError(false))
  }

  /**
    * Throw on JavaScript Error. Preferably use [[HtmlUnitConfiguration.disableJavaScript()]], as the two configurations only make sense when combined.
    */
  override def throwOnJavaScriptError(): Unit = configurations += "throwOnJSError" ->
    ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setThrowExceptionOnScriptError(true))

  /**
    * Silently swallow JavaScript errors. Preferably use [[HtmlUnitConfiguration.disableJavaScript()]], as the two configurations only make sense when combined.
    */
  override def swallowJavaScriptErrors(): Unit = configurations += "throwOnJSError" ->
    ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setThrowExceptionOnScriptError(false))

  /**
    * Enable CSS evaluation in the webDriver.
    */
  override def enableCss(): Unit = configurations += "enableCss" ->
    ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setCssEnabled(true))

  /**
    * Disable CSS evaluation in the webDriver.
    */
  override def disableCss(): Unit = configurations += "enableCss" ->
    ((webDriver: WebDriver) => asWebClientExposingDriverOrError(webDriver).getOptions.setCssEnabled(false))

  private def asWebClientExposingDriverOrError(webDriver: WebDriver): WebClientExposingDriver = webDriver match {
    case w: WebClientExposingDriver => w
    case _ => throw new RuntimeException(s"This configuration can only be applied to a webDriver of type ${classOf[WebClientExposingDriver].getCanonicalName}")
  }
}
