package org.scalawebtest.integration.gauge

import org.scalawebtest.core.gauge.Gauge.fit
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class SimpleGaugeSpec extends ScalaWebTestBaseSpec {

  path = "/navigation.jsp"

  "The navigation" should "contain our navigation links in correct structure, order and with the expected text" in {
    fit(
      <nav id="mainNav">
        <ul>
          <li>
            <a href="/path/to/first/element"></a>
          </li>
          <li>
            <a href="/path/to/second/element"></a>
          </li>
          <li>
            <a href="/path/to/third/element"></a>
          </li>
        </ul>
      </nav>)
  }
}
