package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsValue, Json}

class FitsValuesAndHasOnlyPropertiesSpecifiedSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "Fits types" should "report success, when the json gauge contains the same values as the response it is tested against" in {
    dijkstra fits valuesAndHasOnlyPropertiesSpecified by
      """{
        | "name": "Dijkstra",
        | "firstName": "Edsger",
        | "yearOfBirth": 1930,
        | "isTuringAwardWinner": true,
        | "theories": [
        |   "shortest path",
        |   "graph theory"
        | ],
        | "universities": [
        |   { "name": "Universität Leiden","begin": 1948, "end": 1956 },
        |   { "name": "Mathematisch Centrum Amsterdam", "begin": 1951, "end": 1959 },
        |   { "name": "Technische Universiteit Eindhoven", "begin": 1962, "end": 1984 },
        |   { "name": "University of Texas at Austin", "begin": 1984, "end": 1999 }
        | ],
        | "falseTheories": null
        |}
      """.stripMargin
  }
  it should "fail when a property is present in the testee, which is not specified in the gauge" in {
    assertThrows[TestFailedException] {
      dijkstra fits typesAndHasOnlyPropertiesSpecified by
        //gauge doesn't contain universities
        """{
          | "name": "Dijkstra",
          | "firstName": "Edsger",
          | "yearOfBirth": 1930,
          | "isTuringAwardWinner": true,
          | "theories": [
          |   "shortest path",
          |   "graph theory"
          | ],
          | "falseTheories": null
          |}
      """.stripMargin
    }
  }
  it should behave like jsonGaugeFitting(valuesAndHasOnlyPropertiesSpecified)
}
