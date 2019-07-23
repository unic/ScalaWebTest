package org.scalawebtest.integration.doc._001

import org.openqa.selenium.By
import org.scalawebtest.core.IntegrationFlatSpec

class HomepageSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:9090/scalawebtest")
  path = "/index.html"

  "Our homepage" should "contain a succinct claim" in {
    webDriver
      .findElement(By.tagName("h2"))
      .getText shouldEqual "Reduce the effort needed to write integration tests"
  }
}

object ScastieScalaTestRunner extends App {
  org.scalatest.run(new HomepageSpec())
}
