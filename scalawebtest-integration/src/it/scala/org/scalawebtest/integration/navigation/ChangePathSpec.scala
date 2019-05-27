package org.scalawebtest.integration.navigation

import org.scalatest.OptionValues
import org.scalawebtest.integration.ScalaWebTestBaseSpec

/**
  * Using navigateTo to change the "test" path is discouraged,
  * because navigateTo has to be called in all succeeding tests (although it will only navigateTo another page if needed),
  * or
  */
class ChangePathSpec extends ScalaWebTestBaseSpec with OptionValues {
  path = "/a.jsp"

  "When url is defined it" should "automatically navigate to page A" in {
    find(cssSelector("h1")).value.text shouldEqual "a"
  }
  it should "navigate to page B" in {
    navigateTo("/b.jsp")
    find(cssSelector("h1")).value.text shouldEqual "b"
  }
  it should "navigate to page A again" in {
    navigateTo("/a.jsp")
    find(cssSelector("h1")).value.text shouldEqual "a"
  }
  it should "navigate to page B again" in {
    navigateTo("/b.jsp")
    find(cssSelector("h1")).value.text shouldEqual "b"
  }
}
