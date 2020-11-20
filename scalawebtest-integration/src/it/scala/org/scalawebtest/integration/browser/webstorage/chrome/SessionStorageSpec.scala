package org.scalawebtest.integration.browser.webstorage.chrome

import org.scalatest.AppendedClues
import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.SeleniumChrome
import org.scalawebtest.integration.browser.behaviors.webstorage.{SessionStorageBehaviour, WebStorageBehaviour}

class SessionStorageSpec extends IntegrationFlatSpec with AppendedClues with SeleniumChrome with WebStorageBehaviour with SessionStorageBehaviour {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/sessionstorage.jsp"

  val sessionWebStorageKey: String          = "message"
  val expectedMsg: String                   = "Hello SessionStorage"

  "SessionStorage of SeleniumChrome" should behave like aWebStorage(sessionStorage, sessionWebStorageKey, expectedMsg)
  "SessionStorage of SeleniumChrome" should behave like aSessionStorage(sessionStorage, sessionWebStorageKey)
}