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
package org.scalawebtest.core

/**
  * Provide fixtures to set a specific webdriver configuration for a given function call
  * and restore previous configuration after the function call.
  */
trait WebClientExposingDriverConfigFixtures {
  self: IntegrationSpec =>

  /**
    * Do not follow redirects during the given function call.
    * Whatever setting was used for redirects, will be restored after this function call.
    */
  def withoutFollowingRedirects[X](f: X => Unit): X => Unit = withFollowingRedirectsInternal(f, enableRedirects = false)

  /**
    * Follow redirects during the given function call.
    * Whatever setting was used for redirects, will be restored after this function call.
    */
  def withFollowingRedirects[X](f: X => Unit): X => Unit = withFollowingRedirectsInternal(f, enableRedirects = true)

  /**
    * Disable CSS during the given function call.
    * Whatever setting was used with regard to CSS, will be restored after this function call.
    */
  def withoutCss[X](f: X => Unit): X => Unit = withCssInternal(f, enableCss = false)

  /**
    * Enable CSS during the given function call.
    * Whatever setting was used with regard to CSS, will be restored after this function call.
    */
  def withCss[X](f: X => Unit): X => Unit = withCssInternal(f, enableCss = true)

  /**
    * Disable JavaScript execution during the given function call.
    * Whatever setting was used for JavaScript execution, will be restored after this function call.
    */
  def withoutJavascript[X](f: X => Unit): X => Unit = withJavascript(f, enabled = false)

  /**
    * Enable JavaScript execution during the given function call.
    * Whatever setting was used for JavaScript execution, will be restored after this function call.
    */
  def withJavascript[X](f: X => Unit): X => Unit = withJavascript(f, enabled = true)

  private def withFollowingRedirectsInternal[X](f: X => Unit, enableRedirects: Boolean): X => Unit = {
    x: X => {
      val webClientExposingDriver = asWebClientExposingDriverOrError(webDriver)
      val redirectionEnabled = webClientExposingDriver.getOptions.isRedirectEnabled
      webClientExposingDriver.getOptions.setRedirectEnabled(enableRedirects)
      try {
        f(x)
      } finally {
        webClientExposingDriver.getOptions.setRedirectEnabled(redirectionEnabled)
      }
    }
  }

  private def withCssInternal[X](f: X => Unit, enableCss: Boolean): X => Unit = {
    x: X => {
      val webClientExposingDriver = asWebClientExposingDriverOrError(webDriver)
      val wasEnabled = webClientExposingDriver.getOptions.isCssEnabled
      webClientExposingDriver.getOptions.setCssEnabled(enableCss)
      try {
        f(x)
      } finally {
        webClientExposingDriver.getOptions.setCssEnabled(wasEnabled)
      }
    }
  }

  private def withJavascript[X](f: X => Unit, enabled: Boolean): X => Unit = {
    x: X => {
      val webClientExposingDriver = asWebClientExposingDriverOrError(webDriver)
      val jsEnabled = webClientExposingDriver.getOptions.isJavaScriptEnabled
      webClientExposingDriver.getOptions.setJavaScriptEnabled(enabled)
      try {
        f(x)
      } finally {
        webClientExposingDriver.getOptions.setJavaScriptEnabled(jsEnabled)
      }
    }
  }
}
