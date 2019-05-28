package org.scalawebtest.integration.browser.behaviors

import org.scalatest.OptionValues
import org.scalawebtest.integration.ScalaWebTestBaseSpec

trait WebBrowserBehavior extends OptionValues {
  self: ScalaWebTestBaseSpec =>
  def aWebBrowserWithElementLookup(): Unit = {
    it should "be capable to find a single element in an HTML document" in {
      navigateTo("/")
      find(cssSelector("h1")).value.text shouldBe "ScalaWebTest - Mock Server"
    }
    it should "be capable to find a list of elements in an HTML document" in {
      navigateTo("/elementsList.jsp")
      findAll(cssSelector("li")) should have size 3
    }
  }
}
