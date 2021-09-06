package org.scalawebtest.integration.browser.behaviors
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalawebtest.core.IntegrationFlatSpec

import scala.language.postfixOps

trait SessionStorageBehaviour {
  self: IntegrationFlatSpec =>

  def aSessionStorage(): Unit = {
    val storageKey: String = "message"
    val expectedMsg: String = "Hello SessionStorage"

    it should "navigateTo /sessionstorage.jsp (sessionStorage)" in {
      navigateTo("/sessionstorage.jsp")
    }
    it should "not be null (sessionStorage)" in {
      webDriver.getSessionStorage should not be null
    }
    it should "have size 1 (sessionStorage)" in {
      eventually(timeout(5 seconds)) {
        webDriver.getSessionStorage should have size 1
      }
    }
    it should s"contain $storageKey (sessionStorage)" in {
      webDriver.getSessionStorage.keySet should contain(storageKey)
    }
    it should s"stored item for $storageKey should equal $expectedMsg (sessionStorage)" in {
      webDriver.getSessionStorage.getItem(storageKey) shouldBe expectedMsg
    }
    it should s"not contain the key $storageKey after deletion (sessionStorage)" in {
      webDriver.getSessionStorage.removeItem(storageKey)
      webDriver.getSessionStorage.keySet should not contain storageKey
    }
  }
}
