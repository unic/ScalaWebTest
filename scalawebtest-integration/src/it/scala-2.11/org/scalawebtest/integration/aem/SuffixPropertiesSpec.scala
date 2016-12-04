package org.scalawebtest.integration.aem

import org.scalawebtest.aem.PageProperties
import org.scalawebtest.core.gauge.Gauge.fits
import org.scalawebtest.integration.extensions.aem.AemModuleScalaWebTestBaseSpec

class SuffixPropertiesSpec extends AemModuleScalaWebTestBaseSpec with PageProperties {
  path= "/aem/home/users/a/admin/profile.form.html/aem/content/geometrixx/en/toolbar/profiles/edit"

  "The profile edit page" should "work on the correct user" in {
    fits(<form><input id="profile_familyName" value={(pageProperties \ "familyName").as[String]}></input></form>)
  }
  it should "have the correct title" in {
    fits(<h1>{(suffixProperties \ "jcr:content" \ "jcr:title").as[String]}</h1>)
  }
}
