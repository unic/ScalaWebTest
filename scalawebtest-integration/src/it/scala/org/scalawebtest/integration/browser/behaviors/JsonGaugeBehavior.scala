package org.scalawebtest.integration.browser.behaviors

import org.scalawebtest.core.IntegrationFlatSpec
import play.api.libs.json.{JsValue, Json}
import org.scalawebtest.json.JsonGauge._

trait JsonGaugeBehavior {
  self: IntegrationFlatSpec =>

  def aJsonGauge(): Unit = {
    def json: JsValue = {
      Json.parse(webDriver.getPageSource)
    }

    it should "be capable to handle JSON responses (some browsers wrap this in HTML and need special treatment)" in {
      navigateTo("/jsonResponse.json.jsp")
      json fits values of
        """
          |{
          |  "name": "Dijkstra",
          |  "firstName": "Edsger",
          |  "yearOfBirth": 1930,
          |  "theories": [
          |    "shortest path",
          |    "graph theory"
          |  ],
          |  "isTuringAwardWinner": true,
          |  "universities": [
          |    {
          |      "name": "Universität Leiden",
          |      "begin": 1948,
          |      "end": 1956
          |    },
          |    {
          |      "name": "Mathematisch Centrum Amsterdam",
          |      "begin": 1951,
          |      "end": 1959
          |    },
          |    {
          |      "name": "Technische Universiteit Eindhoven",
          |      "begin": 1962,
          |      "end": 1984
          |    },
          |    {
          |      "name": "University of Texas at Austin",
          |      "begin": 1984,
          |      "end": 1999
          |    }
          |  ],
          |  "falseTheories": null
          |}""".stripMargin
    }
  }
}
