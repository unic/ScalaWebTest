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
import org.jsoup.nodes.Element
import org.jsoup.parser.{Parser => JsoupParser}
import org.openqa.selenium.htmlunit.HtmlUnitWebElement
import org.openqa.selenium.{JavascriptExecutor, WebDriver, WebElement}
import org.scalawebtest.core.gauge.FragmentParser.parseFragment

import scala.jdk.CollectionConverters._
import scala.reflect.Selectable.reflectiveSelectable
import scala.xml.NodeSeq


/**
  * Helper object to provide functions to fluently build a [[org.scalawebtest.core.gauge.Gauge]], to verify an [[org.scalatestplus.selenium.WebBrowser.Element]] instead of a complete document.
  */
object HtmlElementGauge extends HtmlElementGauge

/**
  * Helper trait to provide functions to fluently build a [[org.scalawebtest.core.gauge.Gauge]], to verify an [[org.scalatestplus.selenium.WebBrowser.Element]] instead of a complete document.
  */
trait HtmlElementGauge {
  type Element = {def underlying: WebElement}

  implicit class GaugeFromElement(element: Element) {

    /**
      * Assert that the provided element `fits` the HTML snippet provided as definition for the `Gauge`.
      *
      * For detailed information on how to construct your gauge definition, consult the documentation of [[org.scalawebtest.core.gauge.Gauge#fits Gauge.fits]]
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
    def fits(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = {
      new Gauge(definition).elementFits(parseFragment(extractSource(element)).head)
    }

    def doesNotFit(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = {
      new Gauge(definition).elementDoesNotFit(parseFragment(extractSource(element)).head)
    }

    def fit(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = {
      fits(definition)
    }

    def doesntFit(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = {
      doesNotFit(definition)
    }
  }

  def extractSource(element: Element)(implicit webdriver: WebDriver): String = {
    element.underlying match {
      case htmlUnitElement: HtmlUnitWebElement =>
        val elementField = htmlUnitElement.getClass.getDeclaredField("element")
        elementField.setAccessible(true)
        val domNode = elementField.get(htmlUnitElement).asInstanceOf[DomNode]
        domNode.asXml()
      case _ =>
        val outerHtml = webdriver.asInstanceOf[JavascriptExecutor].executeScript("return arguments[0].outerHTML;", element.underlying).asInstanceOf[String]
        outerHtml
    }
  }
}

/**
  * The FragmentParser is a ScalaWebTest internal helper, taking care of providing the needed context and wrapping, when parsing HTML fragments with Jsoup
  */
object FragmentParser {

  case class ParseFix(selfName: String, context: Option[String], fragmentWrapper: Option[String], modernized: Option[String]) {
    def autoWrapped(fragment: String): String = fragmentWrapper.map(w => s"<$w>$fragment</$w>").getOrElse(fragment)

    def autoModernized: String = modernized.getOrElse(selfName)

    def useContext: String = context.getOrElse("body")
  }

  private def parseFixFor(self: String)(context: Option[String], fragmentWrapper: Option[String] = None, modernized: Option[String] = None) =
    self -> ParseFix(self, context, fragmentWrapper, modernized)

  private val parseFixByElementName: Map[String, ParseFix] =
    Map(
      parseFixFor("rp")(None, Some("ruby")),
      parseFixFor("rt")(None, Some("ruby")),
      parseFixFor("caption")(Some("table")),
      parseFixFor("col")(Some("colgroup")),
      parseFixFor("colgroup")(Some("table")),
      parseFixFor("tbody")(Some("table")),
      parseFixFor("td")(Some("tr")),
      parseFixFor("tfoot")(Some("table")),
      parseFixFor("th")(Some("tr")),
      parseFixFor("thead")(Some("table")),
      parseFixFor("tr")(Some("tbody")),
      parseFixFor("frame")(Some("frameset")),
      parseFixFor("frameset")(Some("html")),
      parseFixFor("image")(None, None, Some("img"))
    )

  def parseFragment(fragment: String): List[Element] = {
    val topElementName: String =
      "<\\w+[\\w-]*".r
        .findFirstIn(fragment)
        .map(_.substring(1))
        .getOrElse("unknown")

    val parseFix: ParseFix = parseFixByElementName.getOrElse(topElementName, ParseFix(topElementName, None, None, None))
    val context = new Element(parseFix.useContext)

    val nodes = JsoupParser.parseFragment(parseFix.autoWrapped(fragment), context, "").asScala

    val unwrappedNodes =
      parseFix.fragmentWrapper match {
        case Some(_) => nodes.flatMap(_.childNodes().asScala.toList)
        case None => nodes
      }

    unwrappedNodes
      .filter(_.nodeName() == parseFix.autoModernized)
      .filter(_.isInstanceOf[Element])
      .map(_.asInstanceOf[Element])
      .toList
  }
}
