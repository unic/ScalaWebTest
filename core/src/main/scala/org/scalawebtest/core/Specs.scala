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

import java.util.logging.Level

import org.openqa.selenium.Cookie
import org.scalatest._
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.selenium.WebBrowser
import org.scalatest.time.SpanSugar._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

/**
  * Base class for integration tests. Please extend this class, together with your extension of IntegrationSettings
  */

abstract class FlatSpecBehavior extends FlatSpec with Matchers with Inspectors
abstract class FreeSpecBehavior extends FreeSpec with Matchers with Inspectors

abstract class IntegrationFlatSpec extends FlatSpecBehavior with IntegrationSpec
abstract class IntegrationFreeSpec extends FreeSpecBehavior with IntegrationSpec

trait IntegrationSpec extends WebBrowser with Suite with BeforeAndAfterEach with BeforeAndAfterAll with WebClientExposingHtmlUnit with IntegrationSettings with Eventually {
  val cookiesToBeDiscarded = new ListBuffer[Cookie]()
  val logger = LoggerFactory.getLogger("IntegrationSpec")

  def loginTimeout = Timeout(60 seconds)

  webDriver.setJavascriptEnabled(false)
  webDriver.getOptions.setThrowExceptionOnScriptError(false)
  webDriver.getOptions.setCssEnabled(false)

  def login() {}

  /**
    * Executed during beforeAll(), before performing any tasks
    */
  def beforeLogin() = {}

  /**
    * Executed during beforeAll(), after logging in.
    */
  def afterLogin() = {}

  /**
    * Sets a cookie for the current test. Any cookie set through this method
    * is discarded after a test.
    */
  def setCookie(name: String, value: String) {
    prepareCookieContext()
    val cookie: Cookie = new Cookie(name, value)
    webDriver.manage().addCookie(cookie)
    cookiesToBeDiscarded += cookie
  }

  def prepareCookieContext() {
    // WebDriver needs to request a HTML page before cookies can be set, as cookies
    // can only be set for a specific domain
    if (pageSource isEmpty) {
      withoutJavascript(withoutCss(webDriver.get))(s"$host/etc/packages.html")
    }
  }

  def deleteCookie(name: String) = {
    webDriver.manage().deleteCookieNamed(name)
  }

  /**
    * Resets all cookies, sets the WCM mode to disabled and logs in again
    */
  def resetCookies() = {
    webDriver.manage().deleteAllCookies()
    login()
    afterLogin()
  }

  def navigateTo(path: String) {
    val targetUrl: String = s"$host$path"
    if (pageSource == null || !(targetUrl equals webDriver.getCurrentUrl)) {
      logger.info("Going to " + targetUrl)
      go to targetUrl
    }
  }

  def withoutFollowingRedirects[X](f: X => Unit): X => Unit = withFollowingRedirectsInternal(f, enableRedirects = false)

  def withFollowingRedirects[X](f: X => Unit): X => Unit = withFollowingRedirectsInternal(f, enableRedirects = true)

  private def withFollowingRedirectsInternal[X](f: (X) => Unit, enableRedirects: Boolean): (X) => Unit = {
    x: X => {
      val redirectionEnabled = webDriver.getOptions.isRedirectEnabled
      webDriver.getOptions.setRedirectEnabled(enableRedirects)
      try {
        f(x)
      } finally {
        webDriver.getOptions.setRedirectEnabled(redirectionEnabled)
      }
    }
  }

  def withoutCss[X](f: X => Unit): X => Unit = withCssInternal(f, enableCss = false)

  def withCss[X](f: X => Unit): X => Unit = withCssInternal(f, enableCss = true)

  private def withCssInternal[X](f: (X) => Unit, enableCss: Boolean): (X) => Unit = {
    x: X => {
      val wasEnabled = webDriver.getOptions.isCssEnabled
      webDriver.getOptions.setCssEnabled(enableCss)
      try {
        f(x)
      } finally {
        webDriver.getOptions.setCssEnabled(wasEnabled)
      }
    }
  }

  def withoutJavascript[X](f: X => Unit): X => Unit = withJavascript(f, enabled = false)

  def withJavascript[X](f: X => Unit): X => Unit = withJavascript(f, enabled = true)

  private def withJavascript[X](f: (X) => Unit, enabled: Boolean): (X) => Unit = {
    x: X => {
      val jsEnabled = webDriver.getOptions.isJavaScriptEnabled
      webDriver.getOptions.setJavaScriptEnabled(enabled)
      try {
        f(x)
      } finally {
        webDriver.getOptions.setJavaScriptEnabled(jsEnabled)
      }
    }
  }

  override def afterEach() {
    cookiesToBeDiscarded.foreach(cookie => webDriver.manage().deleteCookieNamed(cookie.getName))
    cookiesToBeDiscarded.clear()
  }

  /**
    * Overwrite beforeLogin() and afterLogin() for test-specific tasks
    */
  override def beforeAll() {
    beforeLogin()
    avoidLogSpam()
    login()
    afterLogin()
  }

  def avoidLogSpam() {
    java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF)
    java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF)
    java.util.logging.Logger.getLogger("net.lightbody").setLevel(Level.OFF)
  }

}