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
package org.scalawebtest.integration.aem

import org.scalawebtest.aem.PageProperties
import org.scalawebtest.core.gauge.Gauge.fits
import org.scalawebtest.integration.extensions.aem.AemModuleScalaWebTestBaseSpec
import play.api.libs.json.{JsObject, JsValue}

class PagePropertiesSpec extends AemModuleScalaWebTestBaseSpec with PageProperties {
  path = "/aem/geometrixx-outdoors/en/company/our-story.html"

  case class ContentPage(pageProperties: JsValue) {
    def sidebarParsys = (pageProperties \ "jcr:content" \ "sidebar").as[JsObject]
    def jcrTitle =(pageProperties \ "jcr:content" \ "jcr:title").as[String]
  }

  "Content page" should "have the correct title (pageProperties example)" in {
    val contentPage = ContentPage(pageProperties)
    fits(<h1>{contentPage.jcrTitle}</h1>)
  }
  it should "have the correct title (jcrContent example" in {
    fits(<h1>{(pageProperties \ "jcr:content" \ "jcr:title").as[String]}</h1>)
  }
  it should "have the correct title (page model example" in {
    val individualPage = ContentPage(pageProperties)
    fits(<h1>{individualPage.jcrTitle}</h1>)
  }
}
