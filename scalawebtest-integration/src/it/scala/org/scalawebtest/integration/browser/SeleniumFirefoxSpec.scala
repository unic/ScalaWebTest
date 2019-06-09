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
package org.scalawebtest.integration.browser

import org.scalatest.AppendedClues
import org.scalawebtest.core.browser.SeleniumFirefox
import org.scalawebtest.core.gauge.HtmlGauge
import org.scalawebtest.core.{FormBasedLogin, IntegrationFlatSpec}
import org.scalawebtest.integration.browser.behaviors.BrowserBehaviors

class SeleniumFirefoxSpec extends IntegrationFlatSpec with FormBasedLogin with AppendedClues with HtmlGauge with SeleniumFirefox with BrowserBehaviors {
  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")
  path = "/"

  "SeleniumFirefox" should behave like aWebBrowserWithElementLookup()
  it should behave like anHtmlGauge
  it should behave like aJsonGauge
}
