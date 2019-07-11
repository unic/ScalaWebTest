package org.scalawebtest.integration.doc._008

import org.scalawebtest.core.IntegrationFlatSpec

class ElementOrderGauge extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:8080")
  path = "index.html"

  "index.html" should "a correcly ordered list" in {
    currentPage fits
      <div>
        <ul>
          <li>First</li>
          <li>Second</li>
        </ul>
      </div>
  }
}


