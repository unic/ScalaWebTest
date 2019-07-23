package org.scalawebtest.integration.doc._104

import org.scalawebtest.integration.json.{FitsTypeMismatchBehavior, ScalaWebTestJsonBaseSpec}
import play.api.libs.json.{JsValue, Json}

class FitsValuesIgnoringArrayOrderSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  config.useBaseUri("http://localhost:9090")
  path = "/dijkstra.json"

  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "Dijkstra" should "contain the correct firstName and lastName and the correct universities in any order" in {
    dijkstra fits valuesIgnoringArrayOrder of
      """
        |{
        | "name": "Dijkstra",
        | "firstName": "Edsger",
        | "universities":
        | [
        |   { "name": "Universität Leiden","begin": 1948, "end": 1956 },
        |   { "name": "University of Texas at Austin", "begin": 1984, "end": 1999 },
        |   { "name": "Technische Universiteit Eindhoven", "begin": 1962, "end": 1984 },
        |   { "name": "Mathematisch Centrum Amsterdam", "begin": 1951, "end": 1959 }
        | ]
        |}
      """.stripMargin
  }
}
