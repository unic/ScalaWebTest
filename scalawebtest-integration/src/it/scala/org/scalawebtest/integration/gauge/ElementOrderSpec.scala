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

import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ElementOrderSpec extends ScalaWebTestBaseSpec {
  path = "/elementsList.jsp"

  "List" should "contain three items in the correct order" in {
    fits(
      <ul>
        <a href="/test-link1.html"></a>
        <a href="/test-link2.html"></a>
        <a href="/test-link3.html"></a>
      </ul>
    )
  }
  it should "not contain them in the wrong order" in {
    doesnt fit
      <ul>
        <a href="/test-link1.html"></a>
        <a href="/test-link3.html"></a>
        <a href="/test-link2.html"></a>
      </ul>
  }
}
