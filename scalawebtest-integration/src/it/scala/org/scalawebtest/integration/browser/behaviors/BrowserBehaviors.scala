package org.scalawebtest.integration.browser.behaviors

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.gauge.HtmlGauge

trait BrowserBehaviors extends WebBrowserBehavior with HtmlGaugeBehavior with JsonGaugeBehavior with LocalStorageBehaviour with SessionStorageBehaviour {
  self: IntegrationFlatSpec with HtmlGauge =>
}
