package org.scalawebtest.integration.doc._012

import org.scalawebtest.core.{IntegrationFlatSpec, ResponseAccessors}

class ResponseHeaderValueSpec extends IntegrationFlatSpec with ResponseAccessors {
  config.useBaseUri("http://localhost:9090")
  path = "/responseHeaders.jsp"

  "The responseHeaderValue of Content-Type" should "be text/html with charset utf-8" in {
    responseHeaderValue("Content-Type") shouldBe "text/html;charset=utf-8"
  }
  "The responseHeaderValue" should "merge response header field-values, when multiple entries with the same field-name exist" in {
    /**
      * Cache-Control: no-cache
      * Cache-Control: no-store
      *
      * should be merged into
      *
      * Cache-Control: no-cache, no-store
      *
      * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9 for details
      */
    responseHeaderValue("Cache-Control") shouldBe "no-cache, no-store"
  }
}
