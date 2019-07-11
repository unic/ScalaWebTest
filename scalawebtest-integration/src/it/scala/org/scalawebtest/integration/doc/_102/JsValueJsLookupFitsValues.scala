package org.scalawebtest.integration.doc._102

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.json.JsonGauge
import play.api.libs.json.Json

class JsValueJsLookupFitsValues extends IntegrationFlatSpec with JsonGauge {
  path = "/dijkstra.json"
  def dijkstra = Json.parse(webDriver.getPageSource)

  "The response for Dijkstra" should "contain the expected values" in {
    dijkstra fits values of
      """{
          "firstName": "Edsger",
          "name": "Dijkstra",
          "yearOfBirth": 1930,
          "theories": [
              "shortest path",
              "graph theory"
              ]
          }
      """
  }
  it should "contain the correct universities" in {
    val universities = dijkstra \ "universities"
    universities fit values of
      """
        [
            { "name": "Universität Leiden","begin": 1948, "end": 1956 },
            { "name": "Mathematisch Centrum Amsterdam", "begin": 1951, "end": 1959 },
            { "name": "Technische Universiteit Eindhoven", "begin": 1962, "end": 1984 },
            { "name": "University of Texas at Austin", "begin": 1984, "end": 1999 }
        ]
        """
  }
}
