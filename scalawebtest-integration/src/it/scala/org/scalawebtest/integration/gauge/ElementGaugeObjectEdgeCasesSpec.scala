/*
 * Copyright 2019 the original author or authors.
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
package org.scalawebtest.integration.gauge

import org.scalawebtest.core.gauge.HtmlElementGauge._
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ElementGaugeObjectEdgeCasesSpec extends ScalaWebTestBaseSpec {

  config.disableNavigateToBeforeEach()

  "HtmlElementGauge" should "work for fragments with <tr> on top level, although they require <tbody> as parent" in {
    navigateTo("/table.jsp")
    val rows = findAll(CssSelectorQuery("table tr"))

    rows should have size 2 withClue "table.jsp is expected to contain 2 rows"

    for (row <- rows)
      row fits <tr>
        <td>
          <img src="@contains .jpg"></img>
        </td>
      </tr>
  }
  it should "work for fragments with <rp> on top level, although they have to be wrapped in <ruby>" in {
    navigateTo("/ruby.jsp")

    val rps = findAll(CssSelectorQuery("rp"))

    rps should have size 4 withClue "ruby.jsp is expected to contain 4 <rp> tags (wrapping two <rb> tags)"

    for (rp <- rps)
      rp fits <rp>@regex [()]</rp>
  }
  it should "work for the outdated <image> tag, which is automatically modernized to <img>" in {
    navigateTo("/outdatedImageTag.jsp")
    val images = findAll(CssSelectorQuery("div#outdated>img"))

    images should have size 2 withClue "oudatedImageTag.jsp is expected to contain 2 images"
    for (img <- images)
      img fits <image href="@contains .jpg"></image>
  }
}
