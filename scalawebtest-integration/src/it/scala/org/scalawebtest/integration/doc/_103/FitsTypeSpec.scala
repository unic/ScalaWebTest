package org.scalawebtest.integration.doc._103

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.json.JsonGauge
import play.api.libs.json.Json

class FitsTypeSpec extends IntegrationFlatSpec with JsonGauge {
  config.useBaseUri("http://localhost:9090")
  path = "/dijkstra.json"

  def dijkstra = Json.parse(webDriver.getPageSource)

  "The response for Dijkstra" should "contain the expected types" in {
    dijkstra fits types of
      """{
            "firstName": "",
            "name": "",
            "yearOfBirth": 0,
            "theories": [ "" ]
        }
    """
  }
}
