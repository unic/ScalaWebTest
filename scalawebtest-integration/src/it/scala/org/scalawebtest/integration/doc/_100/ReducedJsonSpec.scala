package org.scalawebtest.integration.doc._100

import org.scalawebtest.core.IntegrationFlatSpec
import play.api.libs.json.Json

class ReducedJsonSpec extends IntegrationFlatSpec {
  path = "/dijkstra.json"
  def json = Json.parse(webDriver.getPageSource)

  "Dijkstra" should "have the correct firstname" in {
    def firstName = (json \ "firstName").as[String]
    firstName should equal("Edsger")
  }
}
