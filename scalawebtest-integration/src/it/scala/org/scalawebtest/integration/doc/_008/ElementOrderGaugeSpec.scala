package org.scalawebtest.integration.doc._008

import org.scalawebtest.core.IntegrationFlatSpec
import dotty.xml.interpolator.*

class ElementOrderGaugeSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:9090/elementordergaugespec")
  path = "/index.html"

  "index.html" should "a correctly ordered list" in {
    currentPage fits
      xml"""
        <div>
          <ul>
            <li>First</li>
            <li>Second</li>
          </ul>
        </div>
      """
  }
}


