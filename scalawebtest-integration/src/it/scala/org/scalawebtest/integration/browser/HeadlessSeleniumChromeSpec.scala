package org.scalawebtest.integration.browser

import org.scalatest.AppendedClues
import org.scalawebtest.core.browser.HeadlessSeleniumChrome
import org.scalawebtest.core.gauge.HtmlGauge
import org.scalawebtest.core.{FormBasedLogin, IntegrationFlatSpec}
import org.scalawebtest.integration.browser.behaviors.BrowserBehaviors

class HeadlessSeleniumChromeSpec extends IntegrationFlatSpec with FormBasedLogin with AppendedClues with HtmlGauge with HeadlessSeleniumChrome with BrowserBehaviors {
  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")
  path = "/"

  "SeleniumChrome" should behave like aWebBrowserWithElementLookup()
  it should behave like anHtmlGauge()
  it should behave like aJsonGauge()
}
