package org.scalawebtest.integration.navigation

import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ChangePathSpec extends ScalaWebTestBaseSpec {
  path = "/a.jsp"

  "When url is defined it" should "automatically navigate to page A" in {
    webDriver.findElementByTagName("h1").getText shouldEqual "a"
  }
  afterChangingPathTo("/b.jsp")
  it should "navigate to page B" in {
    webDriver.findElementByTagName("h1").getText shouldEqual "b"
  }
  afterChangingPathTo("/a.jsp")
  it should "navigate to page A again" in {
    webDriver.findElementByTagName("h1").getText shouldEqual "a"
  }
  afterChangingPathTo("/b.jsp")
  it should "navigate to page B again" in {
    webDriver.findElementByTagName("h1").getText shouldEqual "b"
  }
}
