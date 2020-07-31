/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.{JsValue, Json}

class CompletelyFitsValuesIgnoringArrayOrderSpec extends ScalaWebTestJsonBaseSpec with FitsTypeMismatchBehavior {
  path = "/jsonResponse.json.jsp"
  def dijkstra: JsValue = Json.parse(webDriver.getPageSource)

  "Fits types" should "report success, when the json gauge contains the same valuesIgnoringArrayOrder as the response it is tested against" in {
    dijkstra completelyFits valuesIgnoringArrayOrder of
      """{
        | "name": "Dijkstra",
        | "firstName": "Edsger",
        | "yearOfBirth": 1930,
        | "isTuringAwardWinner": true,
        | "theories": [
        |   "graph theory",
        |   "shortest path"
        | ],
        | "universities": [
        |   { "name": "Universität Leiden","begin": 1948, "end": 1956 },
        |   { "name": "University of Texas at Austin", "begin": 1984, "end": 1999 },
        |   { "name": "Technische Universiteit Eindhoven", "begin": 1962, "end": 1984 },
        |   { "name": "Mathematisch Centrum Amsterdam", "begin": 1951, "end": 1959 }
        | ],
        | "falseTheories": null
        |}
      """.stripMargin
  }
  it should "fail when a property is present in the testee, which is not specified in the gauge" in {
    assertThrows[TestFailedException] {
        dijkstra completelyFits valuesIgnoringArrayOrder of
        //gauge doesn't contain universities
        """{
          | "name": "Dijkstra",
          | "firstName": "Edsger",
          | "yearOfBirth": 1930,
          | "isTuringAwardWinner": true,
          | "theories": [
          |   "shortest path",
          |   "graph theory"
          | ],
          | "falseTheories": null
          |}
      """.stripMargin
    }
  }
  it should behave like jsonGaugeCompletelyFitting(valuesIgnoringArrayOrder)
}
