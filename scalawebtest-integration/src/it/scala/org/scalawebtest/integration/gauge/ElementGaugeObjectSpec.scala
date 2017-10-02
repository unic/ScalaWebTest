/*
 * Copyright 2017 the original author or authors.
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

import org.scalawebtest.core.gauge.HtmlElementGauge.GaugeFromElement
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ElementGaugeObjectSpec extends ScalaWebTestBaseSpec {
  path = "/galleryOverview.jsp"

  def images = findAll(CssSelectorQuery("ul div.image_columns"))

  val imageGauge = <div class="columns image_columns">
    <a href="@regex \/gallery\/image\/\d">
      <figure class="obj_aspect_ratio">
        <noscript>
          <img class="obj_full" src="@regex \/image\/\d\.jpg\?w=600"></img>
        </noscript>
        <img class="obj_full lazyload" srcset="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" data-sizes="auto"></img>
      </figure>
    </a>
  </div>

  /**
    * Since it possible to run partial matches for the DOM of an entire page, the same should be true for
    * gauges for parts of the page. Otherwise, the user experience feels inconsistent.
    *
    */
  val partialImageGauge = <figure class="obj_aspect_ratio"></figure>

  "The element gauge" should "successfully verify if single elements fit the given gauge" in {
    images.size should be > 5 withClue " - gallery didn't contain the expected amount of images"

    for (image <- images) {
      image fits partialImageGauge
    }
  }
}
