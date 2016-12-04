/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalawebtest.integration.js

import org.scalatest.time.SpanSugar._
import org.scalawebtest.core.gauge.Gauge.fits
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class AjaxSpec extends ScalaWebTestBaseSpec {
  path = "/simpleAjax.jsp"
  config.enableJavaScript(throwOnError = true)

  "A simple webpage loading content with JS" should "be correctly interpreted by HtmlUnit" in {
    eventually(timeout(3 seconds)) {
      container.text should include("Text loaded with JavaScript")
    }
  }
  it should "work with Gauge.fits" in {
    eventually(timeout(3 seconds)) {
      fits(<div id="container">Text loaded with JavaScript</div>)
    }
  }

  def container = {
    find(cssSelector("div#container")).get
  }
}
