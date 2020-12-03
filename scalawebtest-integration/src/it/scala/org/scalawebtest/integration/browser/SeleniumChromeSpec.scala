package org.scalawebtest.integration.browser

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import org.scalawebtest.integration.browser.behaviors.BrowserBehaviors

class SeleniumChromeSpec extends ScalaWebTestBaseSpec with BrowserBehaviors {

  config.useBaseUri("http://localhost:9090")
  config.enableJavaScript(throwOnError = true)
  config.enforceReloadOnNavigateTo()

  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")
  path = "/"

  "SeleniumChrome" should behave like aWebBrowserWithElementLookup()
  it should behave like anHtmlGauge()
  it should behave like aJsonGauge()
  it should behave like aLocalStorage()
  it should behave like aSessionStorage()
}
