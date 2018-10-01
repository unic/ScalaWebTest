package org.scalawebtest.integration.response

import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.scalawebtest.core.ResponseAccessors
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class CurrentPageSpec extends ScalaWebTestBaseSpec with ResponseAccessors {
  path = "/index.jsp"

  "When opening a HTML page, it" should "be accessible via currentHtmlPage" in {
    currentHtmlPage.get shouldBe a[HtmlPage]
  }
  it should "return None when trying to get the currentXmlPage" in {
    currentXmlPage shouldBe None
  }
}
