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

import org.scalatest.exceptions.TestFailedException
import org.scalatest.{AppendedClues, Assertions, Matchers}
import play.api.libs.json._

import scala.language.implicitConversions

/**
  * Helper object to provide functions to fluently build a [[org.scalawebtest.json.JsonGauge]].
  *
  * ==Overview==
  * Import [[JsonGaugeBuilder$.JsonGaugeFromJsLookup]] or [[JsonGaugeBuilder$.JsonGaugeFromJsValue]], then start
  * with a [[play.api.libs.json.JsLookupResult]]  followed by [[JsonGaugeBuilder$.JsonGaugeFromJsLookup#fits]], [[JsonGaugeBuilder$.JsonGaugeFromJsValue#fit]] or [[JsonGaugeBuilder$.JsonGaugeFromJsValue#containsElementFitting]]
  * or [[play.api.libs.json.JsValue]] followed by [[JsonGaugeBuilder.JsonGaugeFromJsValue#fits]] or [[JsonGaugeBuilder$.JsonGaugeFromJsValue#fit]]
  *
  * Next you choose the [[JsonGaugeBuilder$.GaugeType]], which has to be one of the following [[JsonGaugeBuilder.types$]], [[JsonGaugeBuilder.typesAndArraySizes$]], [[JsonGaugeBuilder.values$]] or [[JsonGaugeBuilder$.JsonGaugeFromJsLookup#containsElementFitting]]
  *
  * Last is the definition of the JSON `gauge` wrapped in [[JsonGaugeFits#of]]. The definition has to be a String, which contains a valid JSON document.
  *
  * ==Example==
  * {{{
  * val dijkstra: JsValue = Json.parse("""{"name": "Dijkstra", "firstName": "Edsger"}""")
  * dijkstra fits values of """{"firstName": "Edsger"}"""
  * }}}
  *
  */
object JsonGaugeBuilder {

  /**
    * Import this implicit class, to build a JsonGauge from a JsLookupResult
    */
  implicit class JsonGaugeFromJsLookup(jsLookup: JsLookupResult) extends JsonGaugeFromPlayJson(json = jsLookup.get) {

    def fits(gaugeType: GaugeType): JsonGaugeFits = JsonGaugeFits(gaugeByType(gaugeType))

    def fit(gaugeType: GaugeType): JsonGaugeFits = fits(gaugeType)

    def containsElementFitting(gaugeType: GaugeType): JsonGaugeArrayContains = JsonGaugeArrayContains(gaugeByType(gaugeType))
  }

  /**
    * Import this implicit class, to build a JsonGauge from a JsValue
    */
  implicit class JsonGaugeFromJsValue(jsValue: JsValue) extends JsonGaugeFromPlayJson(json = jsValue) {
    def fits(gaugeType: GaugeType): JsonGaugeFits = JsonGaugeFits(gaugeByType(gaugeType))

    def fit(gaugeType: GaugeType): JsonGaugeFits = fits(gaugeType)

    def containsElementFitting(gaugeType: GaugeType): JsonGaugeArrayContains = JsonGaugeArrayContains(gaugeByType(gaugeType))
  }

  class JsonGaugeFromPlayJson(json: JsValue) {
    protected def gaugeByType(gaugeType: GaugeType): JsonGauge = gaugeType match {
      case `types` =>
        JsonGauge(
          testee = json,
          fitValues = false,
          fitArraySizes = false)
      case `typesAndArraySizes` =>
        JsonGauge(
          testee = json,
          fitValues = false,
          fitArraySizes = true)
      case `values` =>
        JsonGauge(
          testee = json,
          fitValues = true,
          fitArraySizes = true)
    }
  }

  object types extends GaugeType

  object typesAndArraySizes extends GaugeType

  object values extends GaugeType

  sealed class GaugeType

}

case class JsonGaugeFits(gauge: JsonGauge) {
  def of(definition: String): Unit = gauge.fits(Json.parse(definition))
}

case class JsonGaugeArrayContains(gauge: JsonGauge) extends Assertions with AppendedClues with Matchers {
  def of(definition: String): Unit = {
    gauge.testee match {
      case array: JsArray =>
        if (!hasMatchingElement(array, definition)) {
          fail(s"${gauge.testee.toString()} did not contain an element, which matched the gauge definition $definition")
        }
      case _ => fail(s"${gauge.testee.toString()} expected to be an array, but wasn't.")
    }
  }

  private def hasMatchingElement(array: JsArray, definition: String) = {
    array.value.exists(e => {
      try {
        gauge.withTestee(e).fits(Json.parse(definition))
        //the next line is only reached, if all array elements fitted the definition
        true
      } catch {
        case e: TestFailedException => false //silent catch
      }
    })
  }
}

class PrettyPrintBreadcrumb(breadcrumb: List[String]) {
  def prettyPrint(): String = breadcrumb.reverse.mkString("\\")
}
