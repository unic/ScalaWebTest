package org.scalawebtest.core

import com.gargoylesoftware.htmlunit.TextPage
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.xml.XmlPage
import org.scalatest.Assertions

/**
  * This trait provides convenience methods to access information from the webDriver.
  */
trait ResponseAccessors {
  this: WebClientExposingHtmlUnit with Assertions =>

  /**
    * @return the response code of the web response of the current page
    */
  def responseCode: Int = this.webDriver.getResponseCode

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
  def responseHeaders: Map[String, String] = this.webDriver.getResponseHeaders

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
      s"""The current web response for ${this.webDriver.getCurrentUrl}
        did not contain the expected response header with field-name: '$name'
        It contained the following header field-names: $headerNames""")
  }

  /**
    * @return Some(HtmlPage) if the currentPage is a HtmlPage otherwise None
    */
  def currentHtmlPage: Option[HtmlPage] = this.webDriver.getCurrentHtmlPage

  /**
    * @return Some(TextPage) if the currentPage is a TextPage otherwise None
    */
  def currentTextPage: Option[TextPage] = this.webDriver.getCurrentTextPage

  /**
    * @return Some(XmlPage) if the currentPage is an XmlPage otherwise None
    */
  def currentXmlPage: Option[XmlPage] = this.webDriver.getCurrentXmlPage
}
