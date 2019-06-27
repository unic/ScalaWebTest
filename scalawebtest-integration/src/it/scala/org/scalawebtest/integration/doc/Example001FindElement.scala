package org.scalawebtest.integration.doc

import org.scalawebtest.core.IntegrationFlatSpec
import org.openqa.selenium.By

class HomepageSpec extends IntegrationFlatSpec {
  config.useBaseUri("https://www.scalawebtest.org")
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
