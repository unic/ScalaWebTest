/*
 * Copyright 2016 the original author or authors.
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

import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.util.NameValuePair
import com.gargoylesoftware.htmlunit.xml.XmlPage
import com.gargoylesoftware.htmlunit._
import org.openqa.selenium.html5.{LocalStorage, SessionStorage, WebStorage}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalawebtest.core.browser.webstorage.{HtmlUnitLocalStorage, HtmlUnitSessionStorage, StorageHolderBasedWebStorage}

import scala.jdk.CollectionConverters._

/**
  * Extension of the default HtmlUnitDriver that provides access to some of the web client's options and methods which are hidden in the
  * default implementation.
  */
class WebClientExposingDriver(version: BrowserVersion) extends HtmlUnitDriver(version) with WebStorage {

  /**
    * @return the options object of the WebClient
    */
  def getOptions: WebClientOptions = {
    getWebClient.getOptions
  }

  /**
    *
    * @return the WebClient of this WebDriver instance
    */
  def getClient: WebClient = {
    getWebClient
  }

  /**
    * @return the response code of the web response of the current page
    */
  def getResponseCode: Int = {
    lastPage.getWebResponse.getStatusCode
  }

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
  def getResponseHeaders: Map[String, String] = {
    def toMapWithMergedDuplicates(m: Map[String, String], p: NameValuePair): Map[String, String] = {
      m.get(p.getName) match {
        case Some(v) => m + (p.getName -> s"$v, ${p.getValue}")
        case None => m + (p.getName -> p.getValue)
      }
    }

    lastPage.getWebResponse.getResponseHeaders
      .asScala.foldLeft(Map[String, String]())(toMapWithMergedDuplicates)
  }

  /**
    * @return Some(HtmlPage) if the currentPage is a HtmlPage otherwise None
    */
  def getCurrentHtmlPage: Option[HtmlPage] = {
    lastPage match {
      case page: HtmlPage => Some(page)
      case _ => None
    }
  }

  /**
    * @return Some(TextPage) if the currentPage is a TextPage otherwise None
    */
  def getCurrentTextPage: Option[TextPage] = {
    lastPage match {
      case page: TextPage => Some(page)
      case _ => None
    }
  }

  /**
    * @return Some(XmlPage) if the currentPage is an XmlPage otherwise None
    */
  def getCurrentXmlPage: Option[XmlPage] = {
    lastPage match {
      case page: XmlPage => Some(page)
      case _ => None
    }
  }

  /**
    * @param timeoutMillis the maximum amount of time to wait (in milliseconds)
    * @return the number of background JavaScript jobs still executing or waiting to be executed when this
    *         method returns; will be <tt>0</tt> if there are no jobs left to execute
    */
  def waitForBackgroundJavaScript(timeoutMillis: Long): Int = {
    getWebClient.waitForBackgroundJavaScript(timeoutMillis)
  }

  /**
    * @return Gets the Local storage for the last page it as a WebClientExposingWebStorage.
    */
  override def getLocalStorage: LocalStorage = new HtmlUnitLocalStorage(getWebClient.getStorageHolder, StorageHolder.Type.LOCAL_STORAGE, lastPage())

  /**
    * @return Gets the Session storage for the last page and wraps it as a WebClientExposingWebStorage.
    */
  override def getSessionStorage: SessionStorage = new HtmlUnitSessionStorage(getWebClient.getStorageHolder, StorageHolder.Type.SESSION_STORAGE, lastPage())
}