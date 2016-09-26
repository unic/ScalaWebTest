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
package org.scalawebtest.integration.gauge

import org.scalawebtest.core.Gauge.{doesnt, fits}
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ElementOrderSpec extends ScalaWebTestBaseSpec {
  "Text element order" should "have a correct main navigation" in {
    navigateTo("/textElementOrder.jsp")
    fits(<div>
      <p>
        <a>link</a>
        text-after-link
      </p>
    </div>)
  }
  it should "fit with text element before link" in {
    fits(<div>
      <p>text-before-link
        <a>link</a>
      </p>
    </div>)
  }
  it should "fit with text element after link" in {
    fits(<div>
      <p>text-before-link
        <a>link</a>
        text-after-link
      </p>
    </div>)
  }
  it should "fit with text in parent element" in {
    fits(<div>
      <p>text-before-link link text-after-link</p>
    </div>)
  }
  it should "not fit with missing text element" in {
    doesnt fit <div>
      <p class="not-fitting-element-order">text-before-link
        <a>link</a>
        text-after-link
      </p>
    </div>
  }
  it should "not fit with missing link or text before" in {
    doesnt fit <div>
      <p class="not-fitting-element-order missing-elements">
        text
        <a>link</a>
      </p>
    </div>
  }
  it should "not fit with missing link or text after" in {
    doesnt fit <div>
      <p class="not-fitting-element-order missing-elements">
        <a>link</a>
        text
      </p>
    </div>
  }
}
