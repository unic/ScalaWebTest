package org.scalawebtest.integration.json

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalawebtest.json.JsonGaugeFromResponse.fitsValues

class DocumentFitsValuesSpec extends ScalaWebTestJsonBaseSpec {
  override implicit val webDriver: WebDriver = new HtmlUnitDriver()
  path = "/jsonResponse.json.jsp"

  "FitsValues" should "report success, when the json gauge contains the same values as the response it is tested against" in {
    fitsValues(
      """{
        "name": "Dijkstra",
        "firstName": "Edsger",
        "yearOfBirth": 1930,
        "isTuringAwardWinner": true,
        "theories": [
          "shortest path",
          "graph theory"
        ]
      }"""
    )
  }
}

