package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsValue, Json}

class FitsTypesAndArraySizesAndHasOnlyPropertiesSpecifiedSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "The json response representing Edsger Dijkstra" should "use the correct types" in {
    dijkstra fits typesArraySizesAndHasOnlyPropertiesSpecified by
      """{
        | "name": "",
        | "firstName": "",
        | "isTuringAwardWinner": true,
        | "yearOfBirth": 0,
        | "theories": ["", ""],
        | "universities": [{"name": "", "begin": 0, "end": 0}, {"name": "", "begin": 0, "end": 0}, {"name": "", "begin": 0, "end": 0}, {"name": "", "begin": 0, "end": 0}],
        | "falseTheories": null
        |}
      """.stripMargin
  }
  it should "fail when a property is present in the testee, which is not specified in the gauge" in {
    assertThrows[TestFailedException] {
      dijkstra fits typesArraySizesAndHasOnlyPropertiesSpecified by
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
      dijkstra fits typesArraySizesAndHasOnlyPropertiesSpecified by
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
  it should "not have only a single entry in universities" in {
    assertThrows[TestFailedException] {
      dijkstra fits typesArraySizesAndHasOnlyPropertiesSpecified of
      """{
        | "universities": [{}]
        |}
      """.stripMargin
    }
  }
  it should behave like jsonGaugeFitting(typesArraySizesAndHasOnlyPropertiesSpecified)
}
