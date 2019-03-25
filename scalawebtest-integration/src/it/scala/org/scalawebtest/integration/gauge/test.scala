package org.scalawebtest.integration.gauge

import org.scalawebtest.integration.ScalaWebTestBaseSpec

class test extends ScalaWebTestBaseSpec {

  path = "/navigation.jsp"

  "adfa" should "adsf" in {
    fits(<nav id="mainNav">
      <ul class="blue_theme">
        <li>
          <a href="/path/to/first/element"></a>
        </li>
        <li>
          <a href="/path/to/second/lement"></a>
        </li>
        <li>
          <a href="/path/to/third/element"></a>
        </li>
      </ul>
    </nav>)
  }
}
