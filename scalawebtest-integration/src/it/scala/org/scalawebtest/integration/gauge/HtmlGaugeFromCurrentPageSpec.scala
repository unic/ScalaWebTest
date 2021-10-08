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

import org.scalawebtest.core.IntegrationFlatSpec
import dotty.xml.interpolator.*

class HtmlGaugeFromCurrentPageSpec extends IntegrationFlatSpec {
  config.useBaseUri("http://localhost:9090")

  path = "/index.jsp"

  "CurrentPage" should "fit a matching GaugeDefinition" in {
    currentPage fits xml"""<h1>ScalaWebTest - Mock Server</h1>"""
  }
  it should "also work with fit (synonym of fits)" in {
    currentPage fit xml"""<h1>ScalaWebTest - Mock Server</h1>"""
  }
  it should "not fit a GaugeDefinition with a wrong title" in {
    currentPage doesntFit xml"""<h2>ScalaWebTest - False Text and Tag</h2>"""
  }
  it should """also work with "doesNotFit" (synonym of doesntFit)""" in {
    currentPage doesNotFit xml"""<h2>ScalaWebTest - False Text and Tag</h2>"""
  }
}
