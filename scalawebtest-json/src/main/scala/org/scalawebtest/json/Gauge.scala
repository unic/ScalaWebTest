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
import scala.reflect.ClassTag

/**
  * One should not have to create an instance of [[Gauge]] manually. Use one of the provided Builder.
  * Either [[JsonGauge]] or [[JsonGaugeFromResponse]]
  *
  * @param testee        JsValue to be tested with the gauge
  * @param fitValues     whether the [[testee]] is expected to fit the gauge values
  * @param fitArraySizes whether the [[testee]] is expected to fit the sizes of contained arrays
  * @param specifiedPropertiesOnly whether the [[testee]] is expected to have only properties, which are specified by the gauge
  */
case class Gauge(testee: JsValue, fitValues: Boolean, fitArraySizes: Boolean, ignoreArrayOrder: Boolean, specifiedPropertiesOnly: Boolean) extends Assertions with AppendedClues with Matchers {

  def withTestee(testee: JsValue): Gauge = Gauge(testee, this.fitValues, this.fitArraySizes, this.ignoreArrayOrder, this.specifiedPropertiesOnly)

  def fits(definition: JsValue): Unit = {
    definition match {
      case o: JsObject => fitsObject(testee, Nil, o)
      case a: JsArray => fitsArray(testee, Nil, a)
      case _ => fail(s"Illegal top level element. Should be of type object or array, but wasn't in $testee")
    }
  }

  private def fitsArray(json: JsValue, breadcrumb: List[String], defA: JsArray): Unit = {
    def assertArrayElementsMatch(a: JsArray): Unit = {
      if (fitArraySizes && defA.value.nonEmpty) {
        a.value should have length defA.value.length withClue s"in ${breadcrumb.prettyPrint}"
      }
      if (ignoreArrayOrder) {
        defA.value.foreach(g => {
          val matchingElementFound = a.value.exists(j => {
            try {
              fits(j, breadcrumb, g)
              //the next line is only reached, if all array elements fit the definition
              true
            } catch {
              //silent catch, it is expected that some elements do not fit the provided gauge
              case _: TestFailedException => false
            }
          })
          if (!matchingElementFound)
            fail(s"${a.toString()} did not contain an element, which matched the gauge definition $g")
        })
      } else {
        a.value.zip(defA.value).foreach { case (j, g) => fits(j, breadcrumb, g) }
      }
    }

    json match {
      case ar: JsArray => assertArrayElementsMatch(ar)
      case v => failForTypeMismatch[JsArray](v, breadcrumb)
    }
  }

  private def fitsObject(json: JsValue, breadcrumb: List[String], defO: JsObject): Unit = {
    def assertObjectContains(o: JsObject, breadcrumb: List[String], value: JsValue): Unit = {
      (o \ breadcrumb.head).toOption match {
        case None => fail(s"Expected to contain the key ${breadcrumb.head}, but didn't in $json. Complete selector was ${breadcrumb.prettyPrint}")
        case Some(v) => fits(v, breadcrumb, value)
      }
    }

    def assertGaugeContains(defO: JsObject, breadcrumb: List[String], value: JsValue): Unit = {
      (defO \ breadcrumb.head).toOption match {
        case None => fail(s"Expected to only contain properties, which are specified in the gauge definition, but found an unspecified one with the key ${breadcrumb.head} and value $value in $json. The complete selector was ${breadcrumb.prettyPrint}")
        case Some(_) =>
      }
    }

    json match {
      case o: JsObject =>
        defO.fields.foreach { case (k, v) => assertObjectContains(o, k :: breadcrumb, v) }

        if (specifiedPropertiesOnly) {
          o.fields.foreach {
            case (k, v) => assertGaugeContains(defO, k :: breadcrumb, v)
          }
        }
      case v => failForTypeMismatch[JsObject](v, breadcrumb)
    }
  }

  private def fitsNumber(json: JsValue, breadcrumb: List[String], defN: JsNumber): Unit = {
    json match {
      case n: JsNumber => if (fitValues) {
        n.value shouldEqual defN.value withClue s"in ${breadcrumb.prettyPrint}"
      }
      case v => failForTypeMismatch[JsNumber](v, breadcrumb)
    }
  }

  private def fitsString(json: JsValue, breadcrumb: List[String], defS: JsString): Unit = {
    json match {
      case s: JsString => if (fitValues) {
        s.value shouldEqual defS.value withClue s"in ${breadcrumb.prettyPrint}"
      }
      case v => failForTypeMismatch[JsString](v, breadcrumb)
    }
  }

  private def fitsBoolean(json: JsValue, breadcrumb: List[String], defB: JsBoolean): Unit = {
    json match {
      case b: JsBoolean => if (fitValues) {
        b.value shouldEqual defB.value withClue s"in ${breadcrumb.prettyPrint}"
      }
      case v => failForTypeMismatch[JsBoolean](v, breadcrumb)
    }
  }

  private def fitsNull(json: JsValue, breadcrumb: List[String]): Unit = {
    json shouldBe JsNull withClue s", in the field ${breadcrumb.prettyPrint}"
  }

  def failForTypeMismatch[T: ClassTag](v: JsValue, breadcrumb: List[String]): Unit = {
    v shouldBe a[T] withClue s", as value for the field ${breadcrumb.prettyPrint}"
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


