package org.scalawebtest.integration.doc._007

import org.scalawebtest.core.IntegrationFlatSpec

class GapGaugeSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:8080")
  path = "index.html"

  "index.html" should "contain a nav item for Scala Test" in {
    currentPage fits
      <ul>
        <a href="https://scalatest.org">Scala Test</a>
      </ul>
  }
}

