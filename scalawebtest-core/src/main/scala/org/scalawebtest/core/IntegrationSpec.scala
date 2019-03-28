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

import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{Cookie, WebDriver}
import org.scalatest._
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.WebBrowser
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps



/**
  * This is the base trait for integration specs. The recommended way is to create your own project specific trait, which extends
  * IntegrationFlatSpec or IntegrationFreeSpec, depending on the ScalaTest style which you prefer.
  *
  * In you own implementation you will usually overwrite settings provided by the IntegrationSettings trait,
  * adapted the default configuration available in loginConfig and config,
  * and extend one of the Login traits if applicable.
  */
trait IntegrationSpec extends WebBrowser with Suite with BeforeAndAfterEach with BeforeAndAfterAll with IntegrationSettings with Eventually {
  implicit val webDriver: WebDriver = new HtmlUnitDriver(BrowserVersion.CHROME)

  val logger: Logger = LoggerFactory.getLogger("IntegrationSpec")
  val cookiesToBeDiscarded = new ListBuffer[Cookie]()
  /**
    * Configuration applied before login.
    * Cookies cannot be set in this configuration. The webDriver
    * has to open a connection, before it can set cookies
    */
  val loginConfig = new LoginConfiguration
  /**
    * Configuration applied after login.
    * Cookies may be added here.
    */
  val config = new Configuration

  /**
    * Stores the path with prefixed [[IntegrationSettings.host]] and [[IntegrationSpec.projectRoot]] as url.
    * Before each test [[WebBrowser.goTo()]] with the currently stored url will be executed, but only
    * if [[WebClientExposingDriver.getCurrentUrl]] doesn't equal url.
    */
  def path_=(path: String): Unit = {
    url = s"$host$projectRoot$path"
  }

  def path: String = url.replaceFirst(host, "").replaceFirst(projectRoot, "")

  protected var url: String = ""

  /**
    * Override to encode your project specific login mechanism.
    *
    * Best practice is to create a trait which overrides the login function and extends the trait Login.
    *
    * For the following standard mechanisms an implementation is already available.
    *  - fill login form with your credentials (use FormBasedLogin trait)
    *  - use basic authentication (use BasicAuthLogin trait)
    */
  def login() {}

  /**
    * Executed during beforeAll(), before performing any tasks
    */
  def beforeLogin(): Unit = {}

  /**
    * Executed during beforeAll(), after logging in.
    */
  def afterLogin(): Unit = {}

  def applyConfiguration(config: BaseConfiguration): Unit = {
    config.configurations.values.foreach(configFunction =>
      try {
        configFunction(webDriver)
      }
      catch {
        case e: Exception => logger.error("The following error occurred while applying settings.", e)
      })
  }

  def avoidLogSpam(): Unit = {
    java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF)
    java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF)
    java.util.logging.Logger.getLogger("net.lightbody").setLevel(Level.OFF)
  }

  override def afterEach(): Unit = {
    cookiesToBeDiscarded.foreach(cookie => delete cookie cookie.getName)
    cookiesToBeDiscarded.clear()
  }

  override def afterAll(): Unit = {
    webDriver.quit()
  }

  /**
    * Overwrite beforeLogin() and afterLogin() for test-specific tasks
    */
  override def beforeAll(): Unit = {
    beforeLogin()
    avoidLogSpam()
    applyConfiguration(loginConfig)
    login()
    applyConfiguration(config)
    afterLogin()
  }

  override def beforeEach(): Unit = {
    if (config.navigateToBeforeEachEnabled) {
      navigateToUrl(url)
    }
  }

  /**
    * Sets a cookie for the current test. Any cookie set through this method
    * is discarded after a test.
    */
  def setCookieForSingleTest(name: String, value: String) {
    val cookie: Cookie = new Cookie(name, value)
    webDriver.manage().addCookie(cookie)
    add cookie(name, value)
    cookiesToBeDiscarded += cookie
  }

  /**
    * Resets all cookies, sets the WCM mode to disabled and logs in again
    */
  def resetCookies(): Unit = {
    webDriver.manage().deleteAllCookies()
    login()
    afterLogin()
  }

  /**
    * To navigate to a path during. For most case calling the setter on path is a better solution.
    * Only use this function, if you group tests, which work on different paths/urls. As soon as you
    * introduce navigateTo for one test, you should call it in all succeeding tests. This to make sure,
    * that a test may be executed on its own.
    */
  def navigateTo(newPath: String): Unit = {
    path = newPath
    navigateToUrl(s"$host$projectRoot$path")
  }

  protected def navigateToUrl(targetUrl: String): Unit = {
    if (targetUrl.isEmpty) {
      throw new RuntimeException("NavigateTo was called without path being defined. Either set config.disableNavigateTo or define path")
    }
    if (pageSource == null || !(targetUrl equals webDriver.getCurrentUrl) || config.reloadOnNavigateToEnforced) {
      logger.info("Going to " + targetUrl)
      go to targetUrl
    } else {
      logger.info("Remaining at " + targetUrl + " without refreshing page")
    }
  }

}
