package org.scalawebtest.integration.navigation

import org.scalatest.time.SpanSugar._
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class EnforceNavigateTo extends ScalaWebTestBaseSpec{
  path = "/simpleAjax.jsp"
  config.enableJavaScript(throwOnError = true)
  config.enforceReloadOnNavigateTo()

  "A simple webpage loading content with JS" should "be correctly interpreted by HtmlUnit" in {
    eventually(timeout(3 seconds)) {
      container.text should include("Text loaded with JavaScript")
    }
  }
  it should "not be immediately available, if page was reloaded" in {
    container.text should not include "Text loaded with JavaScript"
  }

  def container = {
    find(cssSelector("div#container")).get
  }
}
