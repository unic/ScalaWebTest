import org.scalawebtest.core.IntegrationFlatSpec

class GapGauge extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:8080")
  path = "gapgauge.html"

  /**
    * the gauge is missing the `ul` between the `div` and `li`.
    * This is valid.
    */
  "index.html" should "contain a nav item for Scala Test" in {
    currentPage fits
      <div>
        <li>
          <a href="https://scalatest.org">Scala Test</a>
        </li>
      </div>
  }
}
