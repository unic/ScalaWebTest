package org.scalawebtest.integration.gauge

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import dotty.xml.interpolator.*

class SiblingsWithoutIdSpec extends ScalaWebTestBaseSpec {

  path = "/siblingsWithoutId.jsp"

  "The HtmlGauge" should "handle ID-less sibling elements of the same type correctly" in {
    fits(
      xml"""
        <select name="foobar" class="select">
          <option value="foo" selected="selected">foo</option>
          <option value="bar">bar</option>
          <option value="baz">baz</option>
        </select>
      """
    )
  }
}
