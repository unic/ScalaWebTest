package org.scalawebtest.integration.browser.webstorage

import java.net.URL

import org.scalatest.AppendedClues
import org.scalawebtest.core.{IntegrationFlatSpec, WebClientExposingHtmlUnit}

class SessionStorageSpec extends IntegrationFlatSpec with AppendedClues {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/sessionstorage.jsp"

  val sessionStorageKey: String            = "message"
  val expectedMsg: String                  = "Hello SessionStorage"

  def sessionStorage = asWebClientExposingDriverOrError(webDriver).sessionStorage(asWebClientExposingDriverOrError(webDriver).getCurrentHtmlPage.get)

  // maybe extract to a Behaviour?
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
    sessionStorage.get(sessionStorageKey) shouldBe expectedMsg
  }

  it should s"not contain the key $sessionStorageKey after deletion" in {
    sessionStorage.remove(sessionStorageKey)
    sessionStorage.keySet() should not contain sessionStorageKey
  }

  // only test which differs in behaviour
  it should "still contain the same key after the driver was closed once" in {
    asWebClientExposingDriverOrError(webDriver).close()
    asWebClientExposingDriverOrError(webDriver).getClient.openWindow(new URL(url), "")
    sessionStorage.keySet() should not contain sessionStorageKey
  }
}
