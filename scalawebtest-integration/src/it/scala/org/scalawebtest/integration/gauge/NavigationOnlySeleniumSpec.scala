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
package org.scalawebtest.integration.gauge

import org.openqa.selenium.By
import org.scalatest.OptionValues
import org.scalawebtest.integration.ScalaWebTestBaseSpec

import scala.jdk.CollectionConverters._

/**
  * Illustrates the effort needed to implement NavigationSpec witout ScalaWebTest.
  * Also showcases how to use Selenium features, which are crucial for navigation.
  */
class NavigationOnlySeleniumSpec extends ScalaWebTestBaseSpec with OptionValues {
  path = "/elementsList.jsp"

  "The navigation" should "contain three items" in {
    val ul = find(cssSelector("ul")).value

    val listItems = ul.underlying.findElements(By.cssSelector("li.list_item"))

    listItems should have size 3
  }
  it should "contain elements with link and description" in {
    val listItems = findAll(cssSelector("li.list_item")).map(_.underlying)

    val links = listItems
      .flatMap(_.findElements(By.cssSelector("a.link")).asScala)
      .filter(_.getAttribute("href").endsWith("/test-link1.html"))

    val linkTitle = links
      .flatMap(_.findElements(By.cssSelector("div.title")).asScala)
      .filter(_.getText.trim == "Link 1").toList

    linkTitle.headOption shouldBe defined
  }
}
