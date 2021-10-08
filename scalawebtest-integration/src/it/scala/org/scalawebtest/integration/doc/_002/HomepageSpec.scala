package org.scalawebtest.integration.doc._002

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.gauge.HtmlGauge
import dotty.xml.interpolator.*

class HomepageSpec extends IntegrationFlatSpec with HtmlGauge {
  config.useBaseUri("http://localhost:9090/scalawebtest")
  path = "/index.html"

  "Our homepage" should "contain a succinct claim" in {
    currentPage fits xml"""<h2>Reduce the effort needed to write integration tests</h2>"""
  }
}

object ScastieScalaTestRunner extends App {
  org.scalatest.run(new HomepageSpec())
}

