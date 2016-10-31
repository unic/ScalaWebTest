package org.scalawebtest.integration.gauge

import org.scalawebtest.core.gauge.Gauge.NotFit
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class LoggedInSpec extends ScalaWebTestBaseSpec{
  path = "/protectedContent.jsp?username=admin&password=secret"

  "When logged in the protectedContent page" should "not show the login form" in {
    not fit <form name="login_form"></form>
  }

}
