package org.scalawebtest.integration.browser

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import org.scalawebtest.integration.browser.behaviors.BrowserBehaviors

class HtmlUnitSpec extends ScalaWebTestBaseSpec with BrowserBehaviors {
  path = "/"

  "HtmlUnit" should behave like aWebBrowserWithElementLookup
  it should behave like anHtmlGauge
  it should behave like aJsonGauge
}
