import org.scalawebtest.core.IntegrationFlatSpec

class HtmlGaugeWithGaps extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:8080")
  path = "htmlgaugewithgaps.html"

  /**
    * the gauge is missing the `ul` between the `div` and `li`. This is valid.
    */
  "index.html" should "contain a nav item for Unic" in {
    currentPage fits
      <div>
        <li>
          <a href="https://unic.com">Unic</a>
        </li>
      </div>
  }
}
