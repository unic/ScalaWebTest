package org.scalawebtest.integration.response

import org.scalatest.exceptions.TestFailedException
import org.scalawebtest.core.ResponseAccessors
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ResponseHeadersSpec extends ScalaWebTestBaseSpec with ResponseAccessors {
  path = "/responseHeaders.jsp"
  "When accessing a website the responseHeaders accessor method" should "provide access to the current response headers" in {
    responseHeaders should not be empty
  }
  it should "provide direct access to specific headers field-value by field-name" in {
      responseHeaderValue("Content-Type") shouldBe "text/html;charset=utf-8"
  }
  it should "respond with a meaningful error message in case we request a response header field-value for a field-name which doesn't exist" in {
    assertThrows[TestFailedException] {
      responseHeaderValue("content-type") shouldBe "text/html;charset=utf-8"
    }
  }
  it should "merge response header field-values, when multiple entries with the same field-name exist. They have to be merged in order of their occurrence" in {
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
