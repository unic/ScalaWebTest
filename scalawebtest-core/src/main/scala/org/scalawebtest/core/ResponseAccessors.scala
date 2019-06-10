/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalawebtest.core

import org.scalatest.Assertions

/**
  * This trait provides convenience methods to access information from the webDriver.
  */
trait ResponseAccessors {
  this: IntegrationSpec with Assertions =>

  /**
    * @return the response code of the web response of the current page
    */
  def responseCode: Int = asWebClientExposingDriverOrError(webDriver).getResponseCode

  /**
    * If a header field-name occurs multiple times, their field-values are merged into a comma-separated list.
    * This is a lot more convenient then returning a List[String] and it is according to RFC 2616 section 4
    * see https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
    *
    * Multiple message-header fields with the same field-name MAY be present in a message if and only if
    * the entire field-value for that header field is defined as a comma-separated list [i.e., #(values)].
    * It MUST be possible to combine the multiple header fields into one "field-name: field-value" pair,
    * without changing the semantics of the message, by appending each subsequent field-value to the first,
    * each separated by a comma. The order in which header fields with the same field-name are received is therefore
    * significant to the interpretation of the combined field value, and thus a proxy MUST NOT change the order
    * of these field values when a message is forwarded.
    *
    * @return response headers as Map header field-name to header field-value. If a header field-name occurs multiple times, their field-values are merged into a comma-separated list.
    */
  def responseHeaders: Map[String, String] = asWebClientExposingDriverOrError(webDriver).getResponseHeaders

  /**
    * @see [[responseHeaders]] for detailed rules on merging of multiple headers with the same field-name
    * @param name field-name of header
    * @return the field-value of the header with the given field-name. If no such header exists, the test will fail with a meaningful error message
    */
  def responseHeaderValue(name: String): String = responseHeaders.get(name) match {
    case Some(v) => v
    case None =>
      val headerNames = responseHeaders.keys.mkString("'", "', '", "'")
      fail(
      s"""The current web response for
        did not contain the expected response header with field-name: '$name'
        It contained the following header field-names: $headerNames""")
  }
}
