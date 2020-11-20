package org.scalawebtest.integration.browser.behaviors.webstorage

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor

trait WebStorageBehaviour {
  self: IntegrationFlatSpec =>

  def aWebStorage(webStorage: WebStorageAccessor, storageKey:String, expectedMsg:String): Unit = {

    it should "not be null" in {
      webStorage should not be null
    }

    it should "have size 1" in {
      webStorage should have size 1
    }

    it should s"contain $storageKey" in {
      webStorage.keySet should contain(storageKey)
    }

    it should s"stored item for $storageKey should equal $expectedMsg" in {
      webStorage.getItem(storageKey) shouldBe expectedMsg
    }

    it should s"not contain the key $storageKey after deletion" in {
      webStorage.removeItem(storageKey)
      webStorage.keySet should not contain storageKey
    }
  }
}