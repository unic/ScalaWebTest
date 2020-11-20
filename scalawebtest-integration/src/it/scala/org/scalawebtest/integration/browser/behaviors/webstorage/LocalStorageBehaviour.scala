package org.scalawebtest.integration.browser.behaviors.webstorage

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor

trait LocalStorageBehaviour {
  self: IntegrationFlatSpec =>

  def aLocalStorage(localStorage: WebStorageAccessor, storageKey: String): Unit = {
    it should "still contain the same key after the browser was closed once" in {
      webDriver.close
      go to "http://www.google.ch"
      localStorage should not be null
      localStorage should have size 1
      localStorage.keySet should contain(storageKey)
    }
  }
}