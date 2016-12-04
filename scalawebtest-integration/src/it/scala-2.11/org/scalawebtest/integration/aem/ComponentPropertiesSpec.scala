package org.scalawebtest.integration.aem

import org.scalawebtest.aem.PageProperties
import org.scalawebtest.core.gauge.Gauge.fits
import org.scalawebtest.integration.extensions.aem.AemModuleScalaWebTestBaseSpec

class ComponentPropertiesSpec extends AemModuleScalaWebTestBaseSpec with PageProperties {
  path = "/aem/content/geometrixx/en/products/triangle/overview/_jcr_content/par.html"

  "The parys" should "contain the expected products" in {
    fits(
      <h3>
        {(componentProperties \ "title" \ "jcr:title").as[String]}
      </h3>
    )
  }
  it should "have the correct values in the pageProperties" in {
    (pageProperties \ "jcr:content" \ "jcr:title").as[String] shouldBe "Overview"
  }
}
