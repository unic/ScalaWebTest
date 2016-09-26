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
import com.gargoylesoftware.htmlunit.xml.XmlPage
import com.gargoylesoftware.htmlunit.{BrowserVersion, TextPage, WebClient, WebClientOptions}
import org.openqa.selenium.htmlunit.HtmlUnitDriver

/**
  * Extension of the default HtmlUnitDriver that provides access to some of the web client's options and methods which are hidden in the
  * default implementation.
  */
class WebClientExposingDriver(version: BrowserVersion) extends HtmlUnitDriver(version) {

  def getOptions: WebClientOptions = {
    getWebClient.getOptions
  }

  def getClient: WebClient = {
    getWebClient
  }

  def getCurrentHtmlPage: Option[HtmlPage] = {
    lastPage match {
      case page: HtmlPage => Some(page)
      case _ => None
    }
  }

  def getCurrentTextPage: Option[TextPage] = {
    lastPage match {
      case page: TextPage => Some(page)
      case _ => None
    }
  }

  def getCurrentXmlPage: Option[XmlPage] = {
    lastPage match {
      case page: XmlPage => Some(page)
      case _ => None
    }
  }

  def waitForBackgroundJavaScript(timeoutMillis: Long): Int = {
    getWebClient.waitForBackgroundJavaScript(timeoutMillis)
  }
}


