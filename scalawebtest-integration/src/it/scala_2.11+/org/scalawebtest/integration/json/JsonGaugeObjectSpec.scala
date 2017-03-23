package org.scalawebtest.integration.json

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import org.scalawebtest.json.JsonGauge._
import play.api.libs.json.{JsValue, Json}

class JsonGaugeObjectSpec extends ScalaWebTestBaseSpec {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "Fits types" should "report success, when the json gauge contains the same values as the response it is tested against" in {
    dijkstra fits values of
      """{
        | "name": "Dijkstra",
        | "firstName": "Edsger",
        | "yearOfBirth": 1930,
        | "isTuringAwardWinner": true,
        | "theories": [
        |   "shortest path",
        |   "graph theory"
        | ]
        |}
      """.stripMargin
  }
}
