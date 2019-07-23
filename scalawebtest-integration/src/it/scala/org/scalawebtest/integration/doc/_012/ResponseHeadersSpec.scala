package org.scalawebtest.integration.doc._012

import org.scalatest.OptionValues
import org.scalawebtest.core.{IntegrationFlatSpec, ResponseAccessors}

class ResponseHeadersSpec extends IntegrationFlatSpec with ResponseAccessors with OptionValues{
  config.useBaseUri("http://localhost:9090")
  path = "/responseHeaders.jsp"

  "The responseHeaders map" should "contain the Content-Type" in {
    responseHeaders should not be empty
    responseHeaders.keySet should contain("Content-Type")
    responseHeaders.get("Content-Type").value shouldBe "text/html;charset=utf-8"
  }
}
