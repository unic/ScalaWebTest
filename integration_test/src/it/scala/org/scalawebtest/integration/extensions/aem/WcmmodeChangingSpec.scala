package org.scalawebtest.integration.extensions.aem

import org.scalatest.time.SpanSugar._
import org.scalawebtest.aem.WcmMode
import org.scalawebtest.core.gauge.Gauge.fit

import scala.language.postfixOps

class WcmmodeChangingSpec extends AemModuleScalaWebTestBaseSpec {

  config.enableJavaScript(throwOnError = true)
  config.disableNavigateToBeforeEach()
  config.enforceReloadOnNavigateTo()

  path = "/cookieVisualizing.jsp"

  "When navigateTo is called with the withWcmMode(DISABLED) fixture, the webBrowser" should "send the according cookie" in {
    withWcmMode(WcmMode.DISABLED)(navigateTo)(path)

    eventually(timeout(1 second)) {
      fit(<li>@contains wcmmode=DISABLED</li>)
    }
  }
  it should "send the wcmmode cookie, when called with withWcmMode(EDIT)" in {
    withWcmMode(WcmMode.EDIT)(navigateTo)(path)

    eventually(timeout(1 second)) {
      fit(<li>@contains wcmmode=EDIT</li>)
    }
  }

}
