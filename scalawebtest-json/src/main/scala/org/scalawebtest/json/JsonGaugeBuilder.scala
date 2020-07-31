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
import org.scalatest.{AppendedClues, Assertions}
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

import scala.language.implicitConversions

/**
  * Helper object to provide functions to fluently build a [[org.scalawebtest.json.Gauge]]. Which in turn is used to test if
  * a [[play.api.libs.json.JsLookupResult]] or [[play.api.libs.json.JsValue]] fits the gauge definition.
  *
  * ==Overview==
  * Import [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsLookup]] or [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsValue]], then follow the documentation of the [[org.scalawebtest.json.JsonGauge]] trait.
  */
object JsonGauge extends JsonGauge

/**
  * Trait which provides functions to fluently build a [[org.scalawebtest.json.Gauge]]. Which in turn is used to test if
  * a [[play.api.libs.json.JsLookupResult]] or [[play.api.libs.json.JsValue]] fits the provided gauge definition.
  *
  * ==Overview==
  * Start with a [[play.api.libs.json.JsLookupResult]] followed by [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsLookup#fits fits]], [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsLookup#fit fit]] or [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsLookup#containsElementFitting containsElementFitting]]
  * or [[play.api.libs.json.JsValue]] followed by [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsValue#fits fits]], [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsValue#fit fit]] or [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsValue#containsElementFitting containsElementFitting]]
  *
  * Next you choose the [[org.scalawebtest.json.JsonGauge.GaugeType]], which has to be one of the following [[org.scalawebtest.json.JsonGauge#types$ types]], [[org.scalawebtest.json.JsonGauge#typesAndArraySizes$ typesAndArraySizes]], [[org.scalawebtest.json.JsonGauge#values$ values]] or [[org.scalawebtest.json.JsonGauge.JsonGaugeFromJsLookup#containsElementFitting containsElementFitting]]
  *
  * Last is the definition of the JSON `gauge` wrapped in [[org.scalawebtest.json.JsonGaugeFits#of of]]. The definition has to be a String, which contains a valid JSON document.
  *
  * ==Example==
  * {{{
  * val dijkstra: JsValue = Json.parse("""{"name": "Dijkstra", "firstName": "Edsger"}""")
  * dijkstra fits values of """{"firstName": "Edsger"}"""
  * }}}
  *
  */
trait JsonGauge {

  /**
    * Implicit class, to build a Gauge from a JsLookupResult
    */
  implicit class JsonGaugeFromJsLookup(jsLookup: JsLookupResult) extends JsonGaugeFromPlayJson(json = jsLookup.get) {
  }

  /**
    * Implicit class, to build a Gauge from a JsValue
    */
  implicit class JsonGaugeFromJsValue(jsValue: JsValue) extends JsonGaugeFromPlayJson(json = jsValue) {
  }

  class JsonGaugeFromPlayJson(json: JsValue) {
    /**
      * Build a JsonGauge, which checks if the testee fits the gaugeDefinition, by the rules of the gaugeType.
      * The testee might contain properties, which are not specified in the gaugeDefinition.
      */
    def fits(gaugeType: GaugeType): JsonGaugeFits = JsonGaugeFits(gaugeByType(gaugeType))

    /**
      * Synonym for @see [[org.scalawebtest.json.JsonGauge.JsonGaugeFromPlayJson#fits]]
      */
    def fit(gaugeType: GaugeType): JsonGaugeFits = fits(gaugeType)

    /**
      * Build a JsonGauge, which checks if the testee fits the gaugeDefinition, by the rules of the gaugeType.
      * More restrictive then @see [[org.scalawebtest.json.JsonGauge.JsonGaugeFromPlayJson#fits]]. All properties
      * of the testee have to be specified by the gaugeDefinition.
      */
    def completelyFits(gaugeType: GaugeType): JsonGaugeFits = JsonGaugeFits(gaugeByType(gaugeType, allPropertiesDefined = true))

    /**
      * Synonym for @see [[org.scalawebtest.json.JsonGauge.JsonGaugeFromPlayJson#completelyFits]]
      */
    def completelyFit(gaugeType: GaugeType): JsonGaugeFits = completelyFits(gaugeType)

    def containsElementFitting(gaugeType: GaugeType): JsonGaugeArrayContains = JsonGaugeArrayContains(gaugeByType(gaugeType))

    protected def gaugeByType(gaugeType: GaugeType, allPropertiesDefined: Boolean = false): Gauge = gaugeType match {
      case `types` =>
        Gauge(
          testee = json,
          fitValues = false,
          fitArraySizes = false,
          ignoreArrayOrder = true,
          allPropertiesDefined = allPropertiesDefined)
      case `typesAndArraySizes` =>
        Gauge(
          testee = json,
          fitValues = false,
          fitArraySizes = true,
          ignoreArrayOrder = true,
          allPropertiesDefined = allPropertiesDefined)
      case `values` =>
        Gauge(
          testee = json,
          fitValues = true,
          fitArraySizes = true,
          ignoreArrayOrder = false,
          allPropertiesDefined = allPropertiesDefined)
      case `valuesIgnoringArrayOrder` =>
        Gauge(
          testee = json,
          fitValues = true,
          fitArraySizes = true,
          ignoreArrayOrder = true,
          allPropertiesDefined = allPropertiesDefined)
    }
  }

  /**
    * marker object to build a gauge, which only verifies by type
    */
  object types extends GaugeType

  /**
    * marker object to build a gauge, which only verifies by type,
    * but checks array sizes as well
    */
  object typesAndArraySizes extends GaugeType

  /**
    * marker object to build a gauge, which verifies values
    */
  object values extends GaugeType

  /**
    * marker object to build a gauge, which verifies values,
    * but ignores their order within arrays
    */
  object valuesIgnoringArrayOrder extends GaugeType

  /**
    * base trait for the marker objects, which are used to select the behavior of the [[org.scalawebtest.json.Gauge]]
    */
  sealed trait GaugeType

}

case class JsonGaugeFits(gauge: Gauge) {
  def of(definition: String): Unit = gauge.fits(Json.parse(definition))
  def of(definition: JsValue): Unit = gauge.fits(definition)
}

case class JsonGaugeArrayContains(gauge: Gauge) extends Assertions with AppendedClues with Matchers {
  def of(definition: String): Unit = {
    of(Json.parse(definition))
  }

  def of(definition: JsValue): Unit = {
    gauge.testee match {
      case array: JsArray =>
        if (!hasMatchingElement(array, definition)) {
          fail(s"${gauge.testee.toString()} did not contain an element, which matched the gauge definition $definition")
        }
      case v => v shouldBe a[JsArray]
    }
  }

  private def hasMatchingElement(array: JsArray, definition: JsValue) = {
    array.value.exists(e => {
      try {
        gauge.withTestee(e).fits(definition)
        //the next line is only reached, if all array elements fit the definition
        true
      } catch {
        //silent catch, it is expected that some elements do not fit the provided gauge
        case _: TestFailedException => false
      }
    })
  }
}

class PrettyPrintBreadcrumb(breadcrumb: List[String]) {
  def prettyPrint: String = breadcrumb.reverse.mkString("'", ".", "'")
}
