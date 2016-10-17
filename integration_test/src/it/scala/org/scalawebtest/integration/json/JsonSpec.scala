/*
 * Copyright 2016 the original author or authors.
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

import org.openqa.selenium.WebDriver
import org.scalawebtest.integration.ScalaWebTestBaseSpec
import play.api.libs.json.Json

class JsonSpec extends ScalaWebTestBaseSpec {
  val model = new Scientist

  "A computer scientist" should "have the correct firstname" in {
    navigateTo("/jsonResponse.json.jsp")
    model.firstName should equal("Edsger")
  }
  it should "have the correct lastname" in {
    model.name should equal("Dijkstra")
  }
  it should "have the correct theories" in {
    model.theories should contain("graph theory")
  }
  it should "have the correct university names" in {
    model.universityNames should contain allOf(
      "Universität Leiden",
      "Mathematisch Centrum Amsterdam",
      "Technische Universiteit Eindhoven",
      "University of Texas at Austin"
      )
  }
}

class Scientist(implicit webDriver: WebDriver) {
  def json = Json.parse(webDriver.getPageSource)

  def firstName = (json \ "firstName").as[String]

  def name = (json \ "name").as[String]

  def theories = (json \ "theories").as[List[String]]

  def universityNames = (json \ "universities" \\ "name").map(_.as[String])
}
