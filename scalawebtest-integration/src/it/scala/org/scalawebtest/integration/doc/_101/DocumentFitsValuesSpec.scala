package org.scalawebtest.integration.doc._101

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.json.JsonGauge
import org.scalawebtest.json.JsonGaugeFromResponse.fitsValues

class DocumentFitsValuesSpec extends IntegrationFlatSpec with JsonGauge {
  config.useBaseUri("http://localhost:9090")
  path = "/dijkstra.json"

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
