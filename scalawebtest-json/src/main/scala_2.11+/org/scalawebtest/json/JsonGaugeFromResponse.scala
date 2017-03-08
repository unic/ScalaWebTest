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
package org.scalawebtest.json

import org.openqa.selenium.WebDriver
import play.api.libs.json.Json

/**
  *
  */
object JsonGaugeFromResponse {
  def fitsTypes(definition: String)(implicit webDriver: WebDriver): Unit = {
    def document = Json.parse(webDriver.getPageSource)
    JsonGauge(document, fitValues = false, fitArraySizes = false).fits(Json.parse(definition))
  }

  def fitsTypesAndArraySizes(definition: String)(implicit webDriver: WebDriver): Unit = {
    def document = Json.parse(webDriver.getPageSource)
    JsonGauge(document, fitValues = false, fitArraySizes = true).fits(Json.parse(definition))
  }

  def fitsValues(definition: String)(implicit webDriver: WebDriver): Unit = {
    def document = Json.parse(webDriver.getPageSource)
    JsonGauge(document, fitValues = true, fitArraySizes = true).fits(Json.parse(definition))
  }
}

