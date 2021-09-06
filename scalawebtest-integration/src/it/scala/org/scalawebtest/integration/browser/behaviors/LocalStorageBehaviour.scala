package org.scalawebtest.integration.browser.behaviors

import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalawebtest.core.IntegrationFlatSpec
import scala.language.postfixOps

trait LocalStorageBehaviour {
  self: IntegrationFlatSpec =>


  def aLocalStorage(): Unit = {
    val storageKey: String = "message"
    val expectedMsg: String = "Hello LocalStorage"

    it should "navigateTo /localwebstorage.jsp (localStorage)" in {
      navigateTo("/localwebstorage.jsp")
    }
    it should "not be null (localStorage)" in {
      webDriver.getLocalStorage should not be null
    }

    it should "have size 1 (localStorage)" in {
      eventually(timeout(5 seconds)){
        webDriver.getLocalStorage should have size 1
      }
    }

    it should s"contain $storageKey (localStorage)" in {
      webDriver.getLocalStorage.keySet should contain(storageKey)
    }

    it should s"stored item for $storageKey should equal $expectedMsg (localStorage)" in {
      webDriver.getLocalStorage.getItem(storageKey) shouldBe expectedMsg
    }

    it should s"not contain the key $storageKey after deletion (localStorage)" in {
      webDriver.getLocalStorage.removeItem(storageKey)
      webDriver.getLocalStorage.keySet should not contain storageKey
    }
  }
}
