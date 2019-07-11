package org.scalawebtest.integration.doc._001

import org.openqa.selenium.By
import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.HtmlUnit

class HomepageSpec extends IntegrationFlatSpec with HtmlUnit{
  config.useBaseUri("http://localhost:4000")
  path = "/index.html"

  "Our homepage" should "contain a succinct claim" in {
    webDriver
      .findElement(By.tagName("h2"))
      .getText shouldEqual "REDUCE the effort needed to write integration tests"
  }
}

object ScastieScalaTestRunner extends App {
  org.scalatest.run(new HomepageSpec())
}
