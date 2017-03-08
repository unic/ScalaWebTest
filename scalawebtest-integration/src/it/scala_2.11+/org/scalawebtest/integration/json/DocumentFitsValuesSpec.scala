package org.scalawebtest.integration.json

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import org.scalawebtest.json.JsonGaugeFromResponse.fitsValues

class DocumentFitsValuesSpec extends ScalaWebTestBaseSpec {
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

