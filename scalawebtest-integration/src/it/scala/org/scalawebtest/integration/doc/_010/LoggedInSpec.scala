package org.scalawebtest.integration.doc._010

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.gauge.HtmlGauge

class LoggedInSpec extends IntegrationFlatSpec with HtmlGauge {
  path = "/protectedContent.jsp?username=admin&password=secret"

  "When logged in the protectedContent page" should "not show the login form" in {
    currentPage doesNotFit <form name="login_form"></form>
  }
}
