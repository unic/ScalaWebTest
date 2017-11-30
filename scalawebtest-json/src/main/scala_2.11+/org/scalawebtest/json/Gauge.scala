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

import org.scalatest.{AppendedClues, Assertions, Matchers}
import play.api.libs.json._

import scala.language.implicitConversions

/**
  * One should not have to create an instance of [[Gauge]] manually. Use one of the provided Builder.
  * Either [[JsonGauge]] or [[JsonGaugeFromResponse]]
  *
  * @param testee JsValue to be tested with the gauge
  * @param fitValues whether the [[testee]] is expected to fit the gauge values
  * @param fitArraySizes whether the [[testee]] is expected to fit the sizes of contained arrays
  */
case class Gauge(testee: JsValue, fitValues: Boolean, fitArraySizes: Boolean) extends Assertions with AppendedClues with Matchers {

  def withTestee(testee: JsValue) = Gauge(testee, this.fitValues, this.fitArraySizes)

  def fits(definition: JsValue) {
    definition match {
      case o: JsObject => fitsObject(testee, Nil, o)
      case a: JsArray => fitsArray(testee, Nil, a)
      case _ => fail(s"Illegal top level element. Should be of type object or array, but wasn't in $testee")
    }
  }

  private def fitsArray(json: JsValue, breadcrumb: List[String], defA: JsArray): Unit = {
    def assertArrayElementsMatch(a: JsArray): Unit = {
      if (fitArraySizes && defA.value.nonEmpty) {
        a.value should have length defA.value.length withClue s"in ${breadcrumb.prettyPrint()}"
      }
      a.value.zip(defA.value).foreach { case (j, g) => fits(j, breadcrumb, g) }
    }

    json match {
      case ar: JsArray => assertArrayElementsMatch(ar)
      case _ => fail(s"${json.toString()} expected to be an array, but wasn't.")
    }
  }

  private def fitsObject(json: JsValue, breadcrumb: List[String], defO: JsObject): Unit = {
    def assertObjectContains(o: JsObject, breadcrumb: List[String], value: JsValue) {
      (o \ breadcrumb.head).toOption match {
        case None => fail(s"Expected to contain the key ${breadcrumb.head}, but didn't in $json. Complete selector was ${breadcrumb.prettyPrint()}")
        case Some(v) => fits(v, breadcrumb, value)
      }
    }

    json match {
      case o: JsObject => defO.fields.foreach { case (k, v) => assertObjectContains(o, k :: breadcrumb, v) }
      case _ => fail(s"${json.toString()} expected to be an object, but wasn't.")
    }
  }

  private def fitsNumber(json: JsValue, breadcrumb: List[String], defN: JsNumber): Unit = {
    json match {
      case n: JsNumber => if (fitValues) {
        n.value shouldEqual defN.value withClue s"in ${breadcrumb.prettyPrint()}"
      }
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain a Number, but it contained $json instead")
    }
  }

  private def fitsString(json: JsValue, breadcrumb: List[String], defS: JsString): Unit = {
    json match {
      case s: JsString => if (fitValues) {
        s.value shouldEqual defS.value withClue s"in ${breadcrumb.prettyPrint()}"
      }
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain a String, but it contained $json instead")
    }
  }

  private def fitsBoolean(json: JsValue, breadcrumb: List[String], defB: JsBoolean): Unit = {
    json match {
      case b: JsBoolean => if (fitValues) {
        b.value shouldEqual defB.value withClue s"in ${breadcrumb.prettyPrint()}"
      }
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain a BOOLEAN, but it contained $json instead")
    }
  }

  private def fitsNull(json: JsValue, breadcrumb: List[String]): Unit = {
    json match {
      case JsNull => //null is null, verifying for value or type are identical
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain NULL, but it contained $json instead")
    }
  }

  def fits(json: JsValue, breadcrumb: List[String], gauge: JsValue): Unit = {
    gauge match {
      case o: JsObject => fitsObject(json, breadcrumb, o)
      case a: JsArray => fitsArray(json, breadcrumb, a)
      case n: JsNumber => fitsNumber(json, breadcrumb, n)
      case s: JsString => fitsString(json, breadcrumb, s)
      case b: JsBoolean => fitsBoolean(json, breadcrumb, b)
      case JsNull => fitsNull(json, breadcrumb)
      case _ => fail("Invalid element in gauge definition")
    }
  }

  implicit def toPrettyPrintBreadcrumb(breadcrumb: List[String]): PrettyPrintBreadcrumb = {
    new PrettyPrintBreadcrumb(breadcrumb)
  }
}


