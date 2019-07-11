package org.scalawebtest.integration.doc._006

import org.scalawebtest.core.IntegrationFlatSpec

class HtmlGaugeSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:8080")
  path = "index.html"

  "index.html" should "contain a nav item for Unic" in {
    currentPage fits
      <div>
        <ul>
          <li>
            <a href="https://unic.com">Unic</a>
          </li>
        </ul>
      </div>
  }
}

