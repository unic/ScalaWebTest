package org.scalawebtest.integration.json

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import play.api.libs.json.Json

class ReducedJsonSpec extends ScalaWebTestBaseSpec {
  path = "/jsonResponse.json.jsp"
  def json = Json.parse(webDriver.getPageSource)

  "Dijkstra" should "have the correct firstname" in {
    def firstName = (json \ "firstName").as[String]
    firstName should equal("Edsger")
  }
}
