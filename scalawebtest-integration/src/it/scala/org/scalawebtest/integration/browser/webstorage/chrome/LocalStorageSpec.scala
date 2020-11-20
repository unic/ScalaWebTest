package org.scalawebtest.integration.browser.webstorage.chrome

import org.scalatest.AppendedClues
import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.SeleniumChrome
import org.scalawebtest.integration.browser.behaviors.webstorage.{LocalStorageBehaviour, WebStorageBehaviour}

class LocalStorageSpec extends IntegrationFlatSpec with AppendedClues with SeleniumChrome with WebStorageBehaviour with LocalStorageBehaviour {

  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  path = "/localwebstorage.jsp"

  val localWebStorageKey: String            = "message"
  val expectedMsg: String                   = "Hello LocalStorage"

  "LocalStorage of Chrome" should behave like aWebStorage(localStorage, localWebStorageKey, expectedMsg)
  "LocalStorage of Chrome" should behave like aLocalStorage(localStorage, localWebStorageKey)
}