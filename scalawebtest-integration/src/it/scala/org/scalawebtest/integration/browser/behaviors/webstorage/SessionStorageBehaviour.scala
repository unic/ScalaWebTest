package org.scalawebtest.integration.browser.behaviors.webstorage

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor

trait SessionStorageBehaviour {
  self: IntegrationFlatSpec =>

  def aSessionStorage(sessionStorage: WebStorageAccessor, storageKey:String): Unit = {
    it should "not contain the same key after the driver was closed once" in {
      webDriver.close
      go to "www.google.ch"
      sessionStorage should not be null
      sessionStorage should have size 0
      sessionStorage.keySet should not contain storageKey
    }
  }
}