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

import java.net.URI
import java.util.logging.Level

import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.html5.WebStorage
import org.openqa.selenium.{Cookie, WebDriver}
import org.scalatest._
import org.scalatest.concurrent.Eventually
import org.scalatestplus.selenium.WebBrowser
import org.scalawebtest.core.configuration.{BaseConfiguration, Configuration, HtmlUnitConfiguration, LoginConfiguration}
import org.scalawebtest.core.gauge.Gauge
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.xml.NodeSeq

/**
  * This is the base trait for integration specs. The recommended way is to create your own project specific trait, which extends
  * IntegrationFlatSpec or IntegrationFreeSpec, depending on the ScalaTest style which you prefer.
  *
  * In you own implementation you will usually overwrite settings provided by the IntegrationSettings trait,
  * adapted the default configuration available in loginConfig and config,
  * and extend one of the Login traits if applicable.
  */
trait IntegrationSpec extends WebBrowser with Suite with BeforeAndAfterEach with BeforeAndAfterAllConfigMap with Eventually {
  implicit var webDriver: WebDriver with WebStorage = new WebClientExposingDriver(BrowserVersion.CHROME)

  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  val cookiesToBeDiscarded = new ListBuffer[Cookie]()
  /**
    * Configuration applied before login.
    * Cookies cannot be set in this configuration. The webDriver
    * has to open a connection, before it can set cookies
    */
  val loginConfig: LoginConfiguration = new LoginConfiguration with HtmlUnitConfiguration
  /**
    * Configuration applied after login.
    * Cookies may be added here.
    */
  val config: Configuration = new Configuration with HtmlUnitConfiguration

  /**
    * [[org.scalawebtest.core.configuration.Configuration#baseUri baseUri]] with [[org.scalawebtest.core.IntegrationSpec#path path]] appended is used as [[org.scalawebtest.core.IntegrationSpec#uri uri]].
    * Per default, the webdriver navigates to [[org.scalawebtest.core.IntegrationSpec#uri uri]], before the test is executed.
    */
  var path: String = ""

  /**
    * @return [[org.scalawebtest.core.IntegrationSpec#uri uri]] as String
    */
  def url: String = uri.toString

  /**
    * @return [[org.scalawebtest.core.configuration.Configuration#baseUri]] + [[org.scalawebtest.core.IntegrationSpec#path path]] as [[java.net.URI]]
    */
  def uri: URI = config.baseUri.resolve(config.baseUri.getPath + path)

  /**
    * @return org.openqa.selenium.WebDriver.getPageSource wrapped in a [[org.scalawebtest.core.IntegrationSpec.CurrentPage CurrentPage]]
    */
  def currentPage: CurrentPage = CurrentPage(webDriver.getPageSource)

  /**
    * The [[org.scalawebtest.core.IntegrationSpec.CurrentPage CurrentPage]] provides convenient access to the source of the current page and to HtmlGauge functions.
    */
  case class CurrentPage(source: String) {
    /**
      * The same as [[org.scalawebtest.core.gauge.HtmlGauge#fits HtmlGauge.fits]], but explicitly using the source of the currentPage.
      *
      * @see [[org.scalawebtest.core.gauge.HtmlGauge#fits HtmlGauge.fits]]
      */
    def fits(definition: NodeSeq): Unit =  new Gauge(definition).fitsCurrentPageSource(source)

    /**
      * The same as [[org.scalawebtest.core.gauge.HtmlGauge#fits HtmlGauge.fits]], but explicitly using the source of the currentPage.
      *
      * @see [[org.scalawebtest.core.gauge.HtmlGauge#fits HtmlGauge.fits]]
      */
    def fit(definition: NodeSeq): Unit =  new Gauge(definition).fitsCurrentPageSource(source)

    /**
      * The same as [[org.scalawebtest.core.gauge.HtmlGauge.doesnt#fit HtmlGauge.doesnt.fit]], but explicitly using the source of the currentPage.
      *
      * @see [[org.scalawebtest.core.gauge.HtmlGauge.doesnt#fit HtmlGauge.doesnt.fit]]
      */
    def doesNotFit(definition: NodeSeq): Unit =  new Gauge(definition).doesNotFitCurrentPageSource(source)

    /**
      * The same as [[org.scalawebtest.core.gauge.HtmlGauge.doesnt#fit HtmlGauge.doesnt.fit]], but explicitly using the source of the currentPage.
      *
      * @see [[org.scalawebtest.core.gauge.HtmlGauge.doesnt#fit HtmlGauge.doesnt.fit]]
      */
    def doesntFit(definition: NodeSeq): Unit =  new Gauge(definition).doesNotFitCurrentPageSource(source)
  }

  /**
    * HtmlUnit reports a lot of unimportant warnings, such as:
    *  'WARNING: Obsolete content type encountered: 'text/javascript'
    *  'WARNING: Link type 'shortcut icon' not supported.'
    * Therefore ScalaWebTest disables HtmlUnit's Logger per default.
    *
    * To see this warnings, overwrite the avoidLogSpam method.
    */
  def avoidLogSpam(): Unit = {
    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
  }

  /**
    * Override to encode your project specific login mechanism.
    *
    * Best practice is to create a trait which overrides the login function and extends the trait Login.
    *
    * For the following standard mechanisms an implementation is already available.
    *  - fill login form with your credentials (use FormBasedLogin trait)
    *  - use basic authentication (use BasicAuthLogin trait)
    */
  def login(configMap: ConfigMap): Unit = {}

  /**
    * Executed during beforeAll(), before performing any tasks
    */
  def beforeLogin(configMap: ConfigMap): Unit = {}

  /**
    * Executed during beforeAll(), after logging in.
    */
  def afterLogin(configMap: ConfigMap): Unit = {}

  /**
    * Executed as first step during beforeAll(), can be used to modify the webdriver, based on information from the ConfigMap
    */
  def prepareWebDriver(configMap: ConfigMap): Unit = {}

  def applyConfiguration(config: BaseConfiguration, configMap: ConfigMap): Unit = {
    config.updateWithConfigMapAndSystemEnvVars(configMap)

    config.configurations.values.foreach(configFunction =>
      try {
        configFunction(webDriver)
      }
      catch {
        case e: Exception => logger.error("The following error occurred while applying settings.", e)
      })
  }

  override def afterEach(): Unit = {
    cookiesToBeDiscarded.foreach(cookie => delete cookie cookie.getName)
    cookiesToBeDiscarded.clear()
  }

  override def afterAll(configMap: ConfigMap): Unit = {
    webDriver.quit()
  }

  /**
    * Overwrite beforeLogin() and afterLogin() for test-specific tasks
    */
  override def beforeAll(configMap: ConfigMap): Unit = {
    avoidLogSpam()
    prepareWebDriver(configMap)
    beforeLogin(configMap)
    applyConfiguration(loginConfig, configMap)
    login(configMap)
    applyConfiguration(config, configMap)
    afterLogin(configMap)
  }

  override def beforeEach(): Unit = {
    if (config.navigateToBeforeEachEnabled) {
      navigateToUri(uri.toString)
    }
  }

  /**
    * Sets a cookie for the current test. Any cookie set through this method
    * is discarded after a test.
    */
  def setCookieForSingleTest(name: String, value: String): Unit = {
    val cookie: Cookie = new Cookie(name, value)
    webDriver.manage().addCookie(cookie)
    add.cookie(name, value)
    cookiesToBeDiscarded += cookie
  }

  /**
    * Resets all cookies and logs in again
    */
  def resetCookies(): Unit = {
    webDriver.manage().deleteAllCookies()
    applyConfiguration(loginConfig, ConfigMap.empty)
    login(ConfigMap.empty)
    applyConfiguration(config, ConfigMap.empty)
    afterLogin(ConfigMap.empty)
  }

  /**
    * To navigate to a path during. For most case calling the setter on path is a better solution.
    * Only use this function, if you group tests, which work on different paths/urls. As soon as you
    * introduce navigateTo for one test, you should call it in all succeeding tests. This to make sure,
    * that a test may be executed on its own.
    */
  def navigateTo(newPath: String): Unit = {
    path = newPath
    navigateToUri(s"${config.baseUri.toString}$path")
  }

  protected def navigateToUri(targetUri: String): Unit = {
    if (targetUri.isEmpty) {
      throw new RuntimeException("NavigateTo was called without path being defined. Either set config.disableNavigateTo or define path")
    }
    if (pageSource == null || !(targetUri equals webDriver.getCurrentUrl) || config.reloadOnNavigateToEnforced) {
      logger.info("Going to " + targetUri)
      go to targetUri
    } else {
      logger.info("Remaining at " + targetUri + " without refreshing page")
    }
  }

  protected def asWebClientExposingDriverOrError(webDriver: WebDriver): WebClientExposingDriver = webDriver match {
    case w: WebClientExposingDriver => w
    case _ => throw new RuntimeException(s"This feature requires a webDriver of type ${classOf[WebClientExposingDriver].getCanonicalName}, but received ${webDriver.getClass.getCanonicalName}")
  }
}
