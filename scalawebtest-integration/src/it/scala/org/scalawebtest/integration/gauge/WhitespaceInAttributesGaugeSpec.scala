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

import org.scalawebtest.integration.ScalaWebTestBaseSpec

/**
  * Some HTML element attributes, such as the class attribute, allow white spaces. Other than separating
  * multiple attribute values, these spaces must be ignored.
  */
class WhitespaceInAttributesGaugeSpec extends ScalaWebTestBaseSpec {
  path = "/nonNormalWhitespaceAttributes.jsp"

  "An element with an empty attribute" should "be matched by a gauge" in {
    fits (
      <div class="">
        <div class="first"></div>
      </div>
    )
    fits (
      <div>
        <div class="first"></div>
      </div>
    )
  }

  "A preceding space" should "not affect a gauge" in {
    fits(<div class="precedingSpace"></div>)
  }

  "A surrounding space" should "not affect a gauge" in {
    fits(<div class="surroundingSpace"></div>)
  }

  "A preceding tab" should "not affect a gauge" in {
    fits(<div class="precedingTab"></div>)
  }

  "A surrounding tab" should "not affect a gauge" in {
    fits(<div class="surroundingSpace"></div>)
  }

  "White spaces" should "be treated as value separators" in {
    fits(
      <div class="space separated"></div>
    )
  }

  "Line breaks" should "be treated like spaces in" in {
    fits(<div class="line breaks"><div id="lineBreakChild"></div></div>)
  }
}
