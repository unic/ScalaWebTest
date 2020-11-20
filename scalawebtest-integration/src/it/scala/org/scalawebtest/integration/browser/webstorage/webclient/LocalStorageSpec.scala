package org.scalawebtest.integration.browser.webstorage.webclient

import org.scalatest.AppendedClues
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor
import org.scalawebtest.core.{IntegrationFlatSpec, WebClientExposingHtmlUnit}
import org.scalawebtest.integration.browser.behaviors.webstorage.{LocalStorageBehaviour, WebStorageBehaviour}

class LocalStorageSpec extends IntegrationFlatSpec with AppendedClues with WebStorageBehaviour with LocalStorageBehaviour {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/localwebstorage.jsp"

  val localWebStorageKey: String = "message"
  val expectedMsg: String = "Hello LocalStorage"
  val localStorage: WebStorageAccessor = asWebClientExposingDriverOrError(webDriver).localStorage()

  "LocalStorage of WebClientExposingDriver" should behave like aWebStorage(localStorage, localWebStorageKey, expectedMsg)
  "LocalStorage of WebClientExposingDriver" should behave like aLocalStorage(localStorage, localWebStorageKey)
}