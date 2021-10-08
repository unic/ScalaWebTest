package org.scalawebtest.integration.doc._007

import org.scalawebtest.core.IntegrationFlatSpec
import dotty.xml.interpolator.*

class GapGaugeSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:9090/gapgaugespec")
  path = "/index.html"

  "index.html" should "contain a nav item for Scala Test" in {
    currentPage fits
      xml"""
        <ul>
          <a href="https://scalatest.org">Scala Test</a>
        </ul>
      """
  }
}

