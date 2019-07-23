package org.scalawebtest.integration.doc._008

import org.scalawebtest.core.IntegrationFlatSpec

class ElementOrderGaugeSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:9090/elementordergaugespec")
  path = "/index.html"

  "index.html" should "a correctly ordered list" in {
    currentPage fits
      <div>
        <ul>
          <li>First</li>
          <li>Second</li>
        </ul>
      </div>
  }
}


