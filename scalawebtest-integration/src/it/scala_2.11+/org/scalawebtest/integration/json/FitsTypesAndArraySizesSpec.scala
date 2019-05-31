package org.scalawebtest.integration.json

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsValue, Json}

class FitsTypesAndArraySizesSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "The json response representing Edsger Dijkstra" should "use the correct types" in {

    dijkstra fits typesAndArraySizes of
      """{
        | "name": "",
        | "firstName": "",
        | "isTuringAwardWinner": true,
        | "theories": ["", ""],
        | "universities": [{"name": "", "begin": 0, "end": 0}, {}, {}, {}]
        |}
      """.stripMargin
  }
  it should "not have only a single entry in universities" in {
    assertThrows[TestFailedException] {
      dijkstra fits typesAndArraySizes of
      """{
        | "universities": [{}]
        |}
      """.stripMargin
    }
  }
  it should behave like jsonGaugeFitting(typesAndArraySizes)
}
