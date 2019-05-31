package org.scalawebtest.integration.browser.behaviors

import org.scalawebtest.integration.ScalaWebTestBaseSpec

trait HtmlGaugeBehavior {
  self: ScalaWebTestBaseSpec =>

  def anHtmlGauge(): Unit = {
    it should "be capable to fit a simple HTML Gauge" in {
      navigateTo("/elementsList.jsp")
      fits(
        <ul>
          <li><div>Link 2</div></li>
        </ul>)
    }
    it should "be capable to fit an HTML Gauge containing a list of items" in {
      fits(
        <ul>
          <li><div>Link 1</div></li>
          <li><div>Link 2</div></li>
          <li><div>Link 3</div></li>
        </ul>)
    }
    it should "be capable to not fit an HTML Gauge, if the element is missing" in {
      doesnt fit
        <ul>
          <li>
            <p>Link1</p>
          </li>
        </ul>
    }
    it should "be capable to not fit an HTML Gauge, if the element order is wrong" in {
      doesnt fit
        <ul>
          <li><div>Link 3</div></li>
          <li><div>Link 1</div></li>
        </ul>
    }
  }
}
