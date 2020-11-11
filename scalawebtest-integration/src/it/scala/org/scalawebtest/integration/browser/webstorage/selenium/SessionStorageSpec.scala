package org.scalawebtest.integration.browser.webstorage.selenium

import org.scalatest.{AppendedClues, ConfigMap}
import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.SeleniumChrome

class SessionStorageSpec extends IntegrationFlatSpec with AppendedClues with SeleniumChrome {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/sessionstorage.jsp"

  val sessionStorageKey: String            = "message"
  val expectedMsg: String                  = "Hello SessionStorage"

  it should "not be null" in {
    sessionStorage should not be null
  }

  it should "have size 1" in {
    sessionStorage should have size 1
  }

  it should s"contain $sessionStorageKey" in {
    sessionStorage.keySet() should contain(sessionStorageKey)
  }

  it should s"stored item for $sessionStorageKey should equal $expectedMsg" in {
    sessionStorage.getItem(sessionStorageKey) shouldBe expectedMsg
  }

  it should s"not contain the key $sessionStorageKey after deletion" in {
    sessionStorage.removeItem(sessionStorageKey)
    sessionStorage.keySet() should not contain sessionStorageKey
  }

  // only test which differs in behaviour
  it should "still contain the same key after the driver was closed once" in {
    quit()
    prepareWebDriver(new ConfigMap(Map()))
    go to url
    sessionStorage.keySet() should not contain sessionStorageKey
  }
}