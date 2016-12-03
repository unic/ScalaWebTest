package org.scalawebtest.integration.extensions.aem

import org.scalatest.time.SpanSugar._
import org.scalawebtest.core.gauge.Gauge.fit

import scala.language.postfixOps

class WcmmodeDefaultSpec extends AemModuleScalaWebTestBaseSpec {

  config.enableJavaScript(throwOnError = true)

  path = "/cookieVisualizing.jsp"

  "As wcmmode DISABLED is default with the AemTweaks, the webBrowser" should "send the according cookie" in {
    eventually(timeout(1 second)) {
      fit(<li>@contains wcmmode</li>)
    }
  }

}
