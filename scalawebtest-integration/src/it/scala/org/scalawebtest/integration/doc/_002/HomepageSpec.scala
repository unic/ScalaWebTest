package org.scalawebtest.integration.doc._002

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.gauge.HtmlGauge

class HomepageSpec extends IntegrationFlatSpec with HtmlGauge {
  config.useBaseUri( "http://www.scalawebtest.org")
  path = "/index.html"

  "Our homepage" should "contain a succinct claim" in {
    currentPage fits <h2>REDUCE the effort needed to write integration tests</h2>
  }
}

object ScastieScalaTestRunner extends App {
  org.scalatest.run(new HomepageSpec())
}

