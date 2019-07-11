package org.scalawebtest.integration.doc._005

import org.openqa.selenium.By
import org.scalawebtest.core.IntegrationFlatSpec

class HomepageSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://www.scalawebtest.org/index.html")

  loginConfig.swallowJavaScriptErrors()
  config.enableJavaScript(throwOnError = true)

  "Our homepage" should "contain a succinct claim" in {
    webDriver
      .findElement(By.tagName("h2"))
      .getText shouldEqual "Reduce the effort needed to write integration tests"
  }
}
