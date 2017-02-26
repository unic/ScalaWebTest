package org.scalawebtest.json

import org.openqa.selenium.WebDriver
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{AppendedClues, Assertions, Matchers}
import play.api.libs.json._

import scala.language.implicitConversions

/**
  * The JsonGauge shall provide comparable functionality to the HTML Gauge.
  */
case class JsonGaugeInternal(testee: JsValue, matchValues: Boolean, matchArraySizes: Boolean) extends Assertions with AppendedClues with Matchers {

  def withTestee(testee: JsValue) = JsonGaugeInternal(testee, this.matchValues, this.matchArraySizes)

  def fits(definition: JsValue) {
    definition match {
      case o: JsObject => matchesObject(testee, Nil, o)
      case a: JsArray => matchesArray(testee, Nil, a)
      case _ => fail(s"Illegal top level element. Should be of type object or array, but wasn't in $testee")
    }
  }

  private def matchesArray(json: JsValue, breadcrumb: List[String], defA: JsArray): Unit = {
    def assertArrayElementsMatch(a: JsArray) = {
      if (matchArraySizes && defA.value.nonEmpty) {
        a.value should have length defA.value.length withClue s"in ${breadcrumb.prettyPrint()}"
      }
      a.value.zip(defA.value).foreach { case (j, g) => matches(j, breadcrumb, g) }
    }

    json match {
      case ar: JsArray => assertArrayElementsMatch(ar)
      case _ => fail(s"${json.toString()} expected to be an array, but wasn't.")
    }
  }

  private def matchesObject(json: JsValue, breadcrumb: List[String], defO: JsObject) = {
    def assertObjectContains(o: JsObject, breadcrumb: List[String], value: JsValue) {
      (o \ breadcrumb.head).toOption match {
        case None => fail(s"Expected to contain the key ${breadcrumb.head}, but didn't in $json. Complete selector was ${breadcrumb.prettyPrint()}")
        case Some(v) => matches(v, breadcrumb, value)
      }
    }

    json match {
      case o: JsObject => defO.fields.foreach { case (k, v) => assertObjectContains(o, k :: breadcrumb, v) }
      case _ => fail(s"${json.toString()} expected to be an object, but wasn't.")
    }
  }

  private def matchesNumber(json: JsValue, breadcrumb: List[String], defN: JsNumber): Unit = {
    json match {
      case n: JsNumber => if (matchValues) {
        n.value shouldEqual defN.value withClue s"in ${breadcrumb.prettyPrint()}"
      }
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain a Number, but it contained $json instead")
    }
  }

  private def matchesString(json: JsValue, breadcrumb: List[String], defS: JsString): Unit = {
    json match {
      case s: JsString => if (matchValues) {
        s.value shouldEqual defS.value withClue s"in ${breadcrumb.prettyPrint()}"
      }
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain a String, but it contained $json instead")
    }
  }

  private def matchesBoolean(json: JsValue, breadcrumb: List[String], defB: JsBoolean): Unit = {
    json match {
      case b: JsBoolean => if (matchValues) {
        b.value shouldEqual defB.value withClue s"in ${breadcrumb.prettyPrint()}"
      }
      case _ => fail(s"Expected ${breadcrumb.prettyPrint()} to contain a BOOLEAN, but it contained $json instead")
    }
  }

  def matches(json: JsValue, breadcrumb: List[String], gauge: JsValue): Unit = {
    gauge match {
      case o: JsObject => matchesObject(json, breadcrumb, o)
      case a: JsArray => matchesArray(json, breadcrumb, a)
      case n: JsNumber => matchesNumber(json, breadcrumb, n)
      case s: JsString => matchesString(json, breadcrumb, s)
      case b: JsBoolean => matchesBoolean(json, breadcrumb, b)
      case _ => fail("Invalid null element in gauge definition")
    }
  }

  implicit def toPrettyPrintBreadcrumb(breadcrumb: List[String]): PrettyPrintBreadcrumb = {
    new PrettyPrintBreadcrumb(breadcrumb)
  }
}

/**
  * Not sure if we should keep this. If so the methods should be aligned with the once in the JsonGaugeBuilder.
  */
object JsonGaugeInternal {
  def fitsTypes(definition: String)(implicit webDriver: WebDriver): Unit = {
    def document = Json.parse(webDriver.getPageSource)

    new JsonGaugeInternal(document, false, false).fits(Json.parse(definition))
  }

  def fitsTypesAndArraySizes(definition: String)(implicit webDriver: WebDriver): Unit = {
    def document = Json.parse(webDriver.getPageSource)

    new JsonGaugeInternal(document, false, true).fits(Json.parse(definition))
  }

  def fitsValues(definition: String)(implicit webDriver: WebDriver): Unit = {
    def document = Json.parse(webDriver.getPageSource)

    new JsonGaugeInternal(document, true, true).fits(Json.parse(definition))
  }
}

object JsonGaugeBuilder {

  implicit class JsonGaugeFromJsLookup(jsLookup: JsLookupResult) extends JsonGaugeFromPlayJson(json = jsLookup.get) {
    def fits(gaugeType: GaugeType): JsonGaugeFits = JsonGaugeFits(gaugeByType(gaugeType))

    def fit(gaugeType: GaugeType): JsonGaugeFits = fits(gaugeType)

    def containsElementFitting(gaugeType: GaugeType): JsonGaugeArrayContains = JsonGaugeArrayContains(gaugeByType(gaugeType))
  }

  implicit class JsonGaugeFromJsValue(jsValue: JsValue) extends JsonGaugeFromPlayJson(json = jsValue) {
    def fits(gaugeType: GaugeType): JsonGaugeFits = JsonGaugeFits(gaugeByType(gaugeType))

    def fit(gaugeType: GaugeType): JsonGaugeFits = fits(gaugeType)

    def containsElementFitting(gaugeType: GaugeType): JsonGaugeArrayContains = JsonGaugeArrayContains(gaugeByType(gaugeType))
  }

  class JsonGaugeFromPlayJson(json: JsValue) {
    protected def gaugeByType(gaugeType: GaugeType): JsonGaugeInternal = gaugeType match {
      case `types` =>
        new JsonGaugeInternal(
          testee = json,
          matchValues = false,
          matchArraySizes = false)
      case `typesAndArraySizes` =>
        new JsonGaugeInternal(
          testee = json,
          matchValues = false,
          matchArraySizes = true)
      case `values` =>
        new JsonGaugeInternal(
          testee = json,
          matchValues = true,
          matchArraySizes = true)
    }
  }

  object types extends GaugeType

  object typesAndArraySizes extends GaugeType

  object values extends GaugeType

  class GaugeType

}

case class JsonGaugeFits(gauge: JsonGaugeInternal) {
  def of(definition: String): Unit = gauge.fits(Json.parse(definition))
}

case class JsonGaugeArrayContains(gauge: JsonGaugeInternal) extends Assertions with AppendedClues with Matchers {
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
