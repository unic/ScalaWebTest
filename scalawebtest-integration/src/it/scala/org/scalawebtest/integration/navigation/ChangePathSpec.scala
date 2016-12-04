package org.scalawebtest.integration.navigation

import org.scalawebtest.integration.ScalaWebTestBaseSpec

/**
  * Using navigateTo to change the "test" path is discouraged,
  * because navigateTo has to be called in all succeeding tests (although it will only navigateTo another page if needed),
  * or
  */
class ChangePathSpec extends ScalaWebTestBaseSpec {
  path = "/a.jsp"

  "When url is defined it" should "automatically navigate to page A" in {
    webDriver.findElementByTagName("h1").getText shouldEqual "a"
  }
  it should "navigate to page B" in {
    navigateTo("/b.jsp")
    webDriver.findElementByTagName("h1").getText shouldEqual "b"
  }
  it should "navigate to page A again" in {
    navigateTo("/a.jsp")
    webDriver.findElementByTagName("h1").getText shouldEqual "a"
  }
  it should "navigate to page B again" in {
    navigateTo("/b.jsp")
    webDriver.findElementByTagName("h1").getText shouldEqual "b"
  }
}
