/*
 * Copyright 2017 the original author or authors.
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
package org.scalawebtest.core.gauge

import com.gargoylesoftware.htmlunit.html.DomNode
import org.openqa.selenium.WebElement
import org.openqa.selenium.htmlunit.HtmlUnitWebElement
import org.scalawebtest.core.WebClientExposingDriver

import scala.language.reflectiveCalls
import scala.xml.NodeSeq

object ElementGaugeBuilder {
  type Element = {def underlying: WebElement}

  implicit class GaugeFromElement(element: Element) {

    def fits(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      val domNode: DomNode = extractDomNode(element: Element)
      new Gauge(definition).elementFits(domNode)
    }

    def doesNotFit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      val domNode: DomNode = extractDomNode(element: Element)
      new Gauge(definition).elementDoesNotFit(domNode)
    }

    def fit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      fits(definition)
    }

    def doesntFit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      doesNotFit(definition)
    }
  }

  private def extractDomNode(element: Element) = {
    val htmlUnitElement = element.underlying.asInstanceOf[HtmlUnitWebElement]
    val elementField = htmlUnitElement.getClass.getDeclaredField("element")
    elementField.setAccessible(true)
    val domNode = elementField.get(htmlUnitElement).asInstanceOf[DomNode]
    domNode
  }
}
