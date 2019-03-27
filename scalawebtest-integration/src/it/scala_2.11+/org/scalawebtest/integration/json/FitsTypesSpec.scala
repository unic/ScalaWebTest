package org.scalawebtest.integration.json

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.libs.json.{JsValue, Json}

class FitsTypesSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  override implicit val webDriver: WebDriver = new HtmlUnitDriver()
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "Fits types" should "report success, when the json gauge contains the same types as the response it is tested against" in {
    dijkstra fits types of
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
  it should "provide the fit synonym" in {
    val universities = dijkstra \ "universities"
    universities fit types of
      """
        | [{
        |   "name": "",
        |   "begin": 0,
        |   "end": 0
        | }]
      """.stripMargin
  }
  it should behave like jsonGaugeFitting(types)

}
