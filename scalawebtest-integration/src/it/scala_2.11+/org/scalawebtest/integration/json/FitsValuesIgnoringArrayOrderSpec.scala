package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsValue, Json}

class FitsValuesIgnoringArrayOrderSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "Fits types" should "report success, when the json gauge contains the same valuesIgnoringArrayOrder as the response it is tested against" in {
    dijkstra fits valuesIgnoringArrayOrder of
      """{
        | "name": "Dijkstra",
        | "firstName": "Edsger",
        | "yearOfBirth": 1930,
        | "isTuringAwardWinner": true,
        | "theories": [
        |   "graph theory",
        |   "shortest path"
        | ],
        | "falseTheories": null
        |}
      """.stripMargin
  }
  it should "provide the fit synonym" in {
    val universities = dijkstra \ "universities"
    universities fit valuesIgnoringArrayOrder of
      """
        | [
        |   { "name": "Universität Leiden","begin": 1948, "end": 1956 },
        |   { "name": "University of Texas at Austin", "begin": 1984, "end": 1999 },
        |   { "name": "Technische Universiteit Eindhoven", "begin": 1962, "end": 1984 },
        |   { "name": "Mathematisch Centrum Amsterdam", "begin": 1951, "end": 1959 }
        | ]
      """.stripMargin
  }
  it should behave like jsonGaugeFitting(valuesIgnoringArrayOrder)
  it should "fail when a String doesn't contain the correct value" in {
    assertThrows[TestFailedException]{
      dijkstra fits valuesIgnoringArrayOrder of """{"name": "Turing"}"""
    }
  }
  it should "fail when an Int doesn't contain the correct value" in {
    assertThrows[TestFailedException]{
      dijkstra fits valuesIgnoringArrayOrder of """{"yearOfBirth": 1995}"""
    }
  }
  it should "fail when a Boolean doesn't contain the correct value" in {
    assertThrows[TestFailedException]{
      dijkstra fits valuesIgnoringArrayOrder of """{"isTuringAwardWinner": false}"""
    }
  }
  it should "fail when an array is not complete" in {
    assertThrows[TestFailedException]{
      dijkstra fits valuesIgnoringArrayOrder of
        """{
          | "theories": ["shortest path"]
          |}""".stripMargin
    }
  }
  it should "fail when an array contains a wrong value" in {
    assertThrows[TestFailedException]{
      dijkstra fits valuesIgnoringArrayOrder of
        """{
          | "theories": ["shortest path", "relational algebra"]
          |}""".stripMargin
    }
  }
}
