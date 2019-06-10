package org.scalawebtest.integration.response

import org.scalawebtest.integration.ScalaWebTestBaseSpec

class CurrentPageSpec extends ScalaWebTestBaseSpec {
  path = "/index.jsp"

  "CurrentPage.source" should "return the same as webdriver.getPageSource" in {
    currentPage.source shouldBe webDriver.getPageSource
  }
}
