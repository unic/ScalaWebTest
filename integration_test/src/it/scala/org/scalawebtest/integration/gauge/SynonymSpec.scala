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

import org.scalawebtest.core.Gauge.{NotFit, doesnt, fit, fits}
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class SynonymSpec extends ScalaWebTestBaseSpec {
  "CheckingGauge" should "work with fits" in {
    navigateTo("/index.jsp")
    fits(<h1>Unic AEM Testing - Mock Server</h1>)
  }
  it should "work with fit (synonym of fits)" in {
    fit(<h1>Unic AEM Testing - Mock Server</h1>)
  }
  it should "work with doesnt fit" in {
    doesnt fit <h2>Unic AEM Testing - False Text and Tag</h2>
  }
  it should "work with not fit (synonym of doesnt fit)" in {
    not fit <h2>Unic AEM Testing - False Text and Tag</h2>
  }
}
