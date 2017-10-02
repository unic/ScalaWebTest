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


/**
  * Helper object to provide functions to fluently build a [[org.scalawebtest.core.gauge.Gauge]], to verify an [[org.scalatest.selenium.WebBrowser.Element]] instead of a complete document.
  */
object HtmlElementGauge extends HtmlElementGauge

/**
  * Helper trait to provide functions to fluently build a [[org.scalawebtest.core.gauge.Gauge]], to verify an [[org.scalatest.selenium.WebBrowser.Element]] instead of a complete document.
  */
trait HtmlElementGauge {
  type Element = {def underlying: WebElement}

  implicit class GaugeFromElement(element: Element) {

    /**
      * Assert that the provided element `fits` the HTML snippet provided as definition for the `Gauge`.
      *
      * For detailed information on how to construct your gauge definition, consult the documentation of [[org.scalawebtest.core.gauge.Gauge$#fits]]
      *
      * ==Example==
      *
      * {{{
      * def images = findAll(CssSelectorQuery("ul div.image_columns"))
      * images.size should be > 5 withClue " - gallery didn't contain the expected amount of images"
      * for (image <- images) {
      *   //this uses GaugeFromElement
      *   image fits <div class="image_columns"><img></img></div>
      * }
      * }}}
      *
      * If you verify all elements of a list of elements found by findAll, remember to verify the search result is of expected length, or at least not empty.
      */
    def fits(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      val domNode: DomNode = extractDomNode(element: Element)
      new Gauge(definition).fits(domNode)
    }

    def doesNotFit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      val domNode: DomNode = extractDomNode(element: Element)
      new Gauge(definition).doesNotFit(domNode)
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
