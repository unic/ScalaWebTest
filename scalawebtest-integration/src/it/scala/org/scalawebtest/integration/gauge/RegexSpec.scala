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
import dotty.xml.interpolator.*

class RegexSpec extends ScalaWebTestBaseSpec {
  path = "/navigation.jsp"

  "Regex" should "work for path matches" in {
    fits(
      xml"""
        <nav>
          <ul>
            <li>
              <a href="@regex \/path\/to\/first\/element">first navigation element</a>
            </li>
          </ul>
        </nav>
      """
    )
  }
  it should "work for text matches" in {
    fits(
      xml"""
        <nav>
          <ul>
            <li>
              <a href="/path/to/first/element">@regex first.* element</a>
            </li>
          </ul>
        </nav>
      """
    )
  }
}
