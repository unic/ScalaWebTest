package org.scalawebtest.integration.doc._010

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.gauge.HtmlGauge
import dotty.xml.interpolator.*

class LoggedInSpec extends IntegrationFlatSpec with HtmlGauge {
  config.useBaseUri("http://localhost:9090")
  path = "/protectedContent.jsp?username=admin&password=secret"

  "When logged in the protectedContent page" should "not show the login form" in {
    currentPage doesNotFit xml"""<form name="login_form"></form>"""
  }
}
