package org.scalawebtest.integration.aem

import org.scalawebtest.aem.PageProperties
import org.scalawebtest.core.gauge.Gauge.fits
import org.scalawebtest.integration.extensions.aem.AemModuleScalaWebTestBaseSpec
import play.api.libs.json.{JsObject, JsValue}

class FindByResourceTypeSpec extends AemModuleScalaWebTestBaseSpec with PageProperties {
  path = "/aem/geometrixx-outdoors/en/company/our-story.html"

  case class ContentPage(pageProperties: JsValue) {
    def sidebarParsys = (pageProperties \ "jcr:content" \ "sidebar").as[JsObject]
    def sidebarImageAltText = {
      val sidebarImages = sidebarParsys findByResourceType "foundation/components/image"
      (sidebarImages.head \ "alt").as[String]
    }
  }

  "Content page" should "have the correct alt text in the sidebar image" in {
    val contentPage = ContentPage(pageProperties)
    fits(<img alt={contentPage.sidebarImageAltText}></img>)
  }
}
