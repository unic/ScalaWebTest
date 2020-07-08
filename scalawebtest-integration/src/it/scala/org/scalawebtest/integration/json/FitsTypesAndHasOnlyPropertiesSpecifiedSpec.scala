package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsValue, Json}

class FitsTypesAndHasOnlyPropertiesSpecifiedSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)
  "Fits types" should "report success, when the json gauge contains the same types as the response it is tested against" in {

    dijkstra fits typesAndHasOnlyPropertiesSpecified by
      """{
        | "name": "",
        | "firstName": "",
        | "yearOfBirth": 0,
        | "isTuringAwardWinner": true,
        | "theories": [],
        | "universities": [{"name": "", "begin": 0, "end": 0}],
        | "falseTheories": null
        |}
      """.stripMargin
  }
  it should "fail when a property is present in the testee, which is not specified in the gauge" in {
    assertThrows[TestFailedException] {
      dijkstra fits typesAndHasOnlyPropertiesSpecified by
        //gauge doesn't contain yearOfBirth
        """{
          | "name": "",
          | "firstName": "",
          | "isTuringAwardWinner": true,
          | "theories": [],
          | "universities": [{"name": "", "begin": 0, "end": 0}],
          | "falseTheories": null
          |}
      """.stripMargin
    }
  }
  it should "fail when a property is present in the testee, which is not specified in the gauge, even when nested within an array" in {
    assertThrows[TestFailedException] {
      dijkstra fits typesAndHasOnlyPropertiesSpecified by
        //gauge doesn't contain the name property in the universities array
        """{
          | "name": "",
          | "firstName": "",
          | "yearOfBirth": 0,
          | "isTuringAwardWinner": true,
          | "theories": [],
          | "universities": [{"begin": 0, "end": 0}],
          | "falseTheories": null
          |}
      """.stripMargin
    }
  }
  it should behave like jsonGaugeFitting(typesAndHasOnlyPropertiesSpecified)
}
