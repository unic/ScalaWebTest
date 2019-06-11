import org.scalawebtest.core.IntegrationFlatSpec

class HtmlGauge extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:8080")
  path = "htmlgauge.html"

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
