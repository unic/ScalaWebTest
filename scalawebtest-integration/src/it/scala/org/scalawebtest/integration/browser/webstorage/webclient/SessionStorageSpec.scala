package org.scalawebtest.integration.browser.webstorage.webclient

import org.scalatest.AppendedClues
import org.scalawebtest.core.{IntegrationFlatSpec, WebClientExposingHtmlUnit}
import org.scalawebtest.core.browser.SeleniumChrome
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor
import org.scalawebtest.integration.browser.behaviors.webstorage.{SessionStorageBehaviour, WebStorageBehaviour}

class SessionStorageSpec extends IntegrationFlatSpec with AppendedClues with WebStorageBehaviour with SessionStorageBehaviour {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/sessionstorage.jsp"

  val sessionWebStorageKey: String = "message"
  val expectedMsg: String = "Hello SessionStorage"
  val sessionStorage: WebStorageAccessor = asWebClientExposingDriverOrError(webDriver).sessionStorage()

  "SessionStorage of WebClientExposingDriver" should behave like aWebStorage(sessionStorage, sessionWebStorageKey, expectedMsg)
  "SessionStorage of WebClientExposingDriver" should behave like aSessionStorage(sessionStorage, sessionWebStorageKey)
}