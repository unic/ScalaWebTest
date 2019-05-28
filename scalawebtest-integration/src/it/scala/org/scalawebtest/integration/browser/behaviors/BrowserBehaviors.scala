package org.scalawebtest.integration.browser.behaviors

import org.scalawebtest.integration.ScalaWebTestBaseSpec

trait BrowserBehaviors extends WebBrowserBehavior with HtmlGaugeBehavior with JsonGaugeBehavior {
  self: ScalaWebTestBaseSpec =>
}
