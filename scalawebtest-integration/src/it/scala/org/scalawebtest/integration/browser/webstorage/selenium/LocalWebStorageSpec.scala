package org.scalawebtest.integration.browser.webstorage.selenium

import org.scalatest.{AppendedClues, ConfigMap}
import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.SeleniumChrome
import org.scalawebtest.core.gauge.HtmlGauge

class LocalWebStorageSpec extends IntegrationFlatSpec with AppendedClues with HtmlGauge with SeleniumChrome {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/localwebstorage.jsp"

  val localWebStorageKey: String            = "message"
  val expectedMsg: String                   = "Hello LocalStorage"

  it should "not be null" in {
    localWebStorage should not be null
  }

  it should "have size 1" in {
    localWebStorage should have size 1
  }

  it should s"contain $localWebStorageKey" in {
    localWebStorage.keySet() should contain(localWebStorageKey)
  }

  it should s"stored item for $localWebStorageKey should equal $expectedMsg" in {
    localWebStorage.getItem(localWebStorageKey) shouldBe expectedMsg
  }

  it should s"not contain the key $localWebStorageKey after deletion" in {
    localWebStorage.removeItem(localWebStorageKey)
    localWebStorage.keySet() should not contain localWebStorageKey
  }

  // only test which differs in behaviour
//  it should "still contain the same key after the driver was closed once" in {
//    close()
//    prepareWebDriver(new ConfigMap(Map()))
//    go to url
//    localWebStorage should have size 1
//    localWebStorage.keySet() should contain(localWebStorageKey)
//  }
}