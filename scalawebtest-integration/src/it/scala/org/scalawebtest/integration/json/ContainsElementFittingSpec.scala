package org.scalawebtest.integration.json

import play.api.libs.json.{JsLookupResult, JsValue, Json}

class ContainsElementFittingSpec extends ScalaWebTestJsonBaseSpec {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)
  def universities: JsLookupResult = { dijkstra \ "universities" }

  "The universities array" should "contain an element with the expected types" in {
    universities containsElementFitting types of
      """{
        | "name": "",
        | "begin": 0,
        | "end": 0
        | } """.stripMargin
  }
  it should "contain an element with the expected values" in {
    universities containsElementFitting values of
      """{
        | "name": "Technische Universiteit Eindhoven",
        | "begin": 1962,
        | "end": 1984
        | }""".stripMargin
  }
}
