package org.scalawebtest.integration.json

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import org.scalawebtest.json.JsonGaugeFromResponse._

class JsonGaugeFromResponseObjectSpec extends ScalaWebTestBaseSpec {
  path = "/jsonResponse.json.jsp"
  val dijkstraGaugeDefinition =
    """{
        "name": "Dijkstra",
        "firstName": "Edsger",
        "yearOfBirth": 1930,
        "isTuringAwardWinner": true,
        "universities": [
          { "name": "Universität Leiden","begin": 1948, "end": 1956 },
          { "name": "Mathematisch Centrum Amsterdam", "begin": 1951, "end": 1959 },
          { "name": "Technische Universiteit Eindhoven", "begin": 1962, "end": 1984 },
          { "name": "University of Texas at Austin", "begin": 1984, "end": 1999 }
        ],
        "theories": [
          "shortest path",
          "graph theory"
        ],
        "falseTheories": null
      }"""

  "FitsValues" should "report success, when the json gauge contains the same values as the response it is tested against" in {
    fitsValues(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same values as the response it is tested against and the response does not contain unspecified properties" in {
    fitsValuesAndHasOnlySpecifiedProperties(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same values as the response it is tested against, but the order in arrays is ignored" in {
    fitsValuesIgnoringArrayOrders(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same values as the response it is tested against and the response does not contain unspecified properties, but the order in arrays is ignored" in {
    fitsValuesIgnoringArrayOrdersAndHavingOnlySpecifiedProperties(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same types as the response it is tested against" in {
    fitsTypes(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same types as the response it is tested against and the response does not contain unspecified properties" in {
    fitsTypesAndHasOnlySpecifiedProperties(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same types and array sizes as the response it is tested against" in {
    fitsTypesAndArraySizes(dijkstraGaugeDefinition)
  }
  it should "report success, when then json gauge contain the same types and array sizes as the response it is tested against and the response does not contain unspecified properties" in {
    fitsTypesArraySizesAndHasOnlySpecifiedProperties(dijkstraGaugeDefinition)
  }
}

