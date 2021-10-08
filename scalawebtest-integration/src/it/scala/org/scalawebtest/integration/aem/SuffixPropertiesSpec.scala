package org.scalawebtest.integration.aem

import org.scalawebtest.aem.PageProperties
import org.scalawebtest.core.gauge.HtmlGauge.fits
import org.scalawebtest.integration.extensions.aem.AemModuleScalaWebTestBaseSpec
import dotty.xml.interpolator.*

class SuffixPropertiesSpec extends AemModuleScalaWebTestBaseSpec with PageProperties {
  path= "/aem/home/users/a/admin/profile.form.html/aem/content/geometrixx/en/toolbar/profiles/edit"

  "The profile edit page" should "work on the correct user" in {
    fits(xml"""<form><input id="profile_familyName" value=${(pageProperties \ "familyName").as[String]}></input></form>""")
  }
  it should "have the correct title" in {
    fits(xml"""<h1>${(suffixProperties \ "jcr:content" \ "jcr:title").as[String]}</h1>""")
  }
}
