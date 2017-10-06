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
package org.scalawebtest.core.gauge

import com.gargoylesoftware.htmlunit.html.{DomElement, DomNode, DomText}
import org.scalatest.Assertions
import org.scalatest.words.NotWord
import org.scalawebtest.core.WebClientExposingDriver
import org.w3c._

import scala.collection.JavaConverters._
import scala.language.reflectiveCalls
import scala.xml._

/**
  * Gauge provides functions to write integration tests with very low effort. For a detailed description of it's usage,
  * see [[org.scalawebtest.core.gauge.Gauge.fits]] and [[org.scalawebtest.core.gauge.Gauge.doesnt.fit]]
  * as well as [[org.scalawebtest.core.gauge.HtmlElementGauge$.GaugeFromElement.fits]] and [[org.scalawebtest.core.gauge.HtmlElementGauge$.GaugeFromElement.fits]]
  */
class Gauge(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver) extends Assertions {
  type MisfitRelevance = Int
  val MISFIT_RELEVANCE_START_VALUE: MisfitRelevance = 0
  val misfitHolder = new MisfitHolder

  case class Fit(domNode: DomNode, misfitRelevance: MisfitRelevance)

  def fits(): Unit = {
    var fittingNodes = List[Fit]()
    if (webDriver.getCurrentHtmlPage.isEmpty) fail("Current page is not of type HtmlPage. At the moment only Html documents are supported")
    val currentPage = webDriver.getCurrentHtmlPage.get
    definition.theSeq.foreach(gaugeElement => {
      val fittingNode = nodeFits(currentPage.getDocumentElement, gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
      if (fittingNode.isEmpty) {
        failAndReportMisfit()
      }
      else {
        fittingNodes = fittingNode.get :: fittingNodes
        //when proceeding to the next definition, old misfits do not matter anymore
        misfitHolder.wipe()
      }
    })
  }

  def doesNotFit(): Unit = {
    if (webDriver.getCurrentHtmlPage.isEmpty) fail("Current page is not of type HtmlPage. At the moment only Html documents are supported")
    val currentPage = webDriver.getCurrentHtmlPage.get
    definition.theSeq.foreach(gaugeElement => {
      val fit = nodeFits(currentPage.getDocumentElement, gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
      if (fit.isDefined) {
        fail(s"Current document matches the provided gauge, although expected not to!\n Gauge spec: $gaugeElement\n Fitting node: ${fit.get.domNode.prettyString()} found")
      }
      //when proceeding to the next definition, old misfits do not matter anymore
      misfitHolder.wipe()
    })
  }

  def elementFits(domNode: DomNode): Unit = {
    var fittingNodes = List[Fit]()
    definition.theSeq.foreach(gaugeElement => {
      val fittingNode = nodeFits(Option(domNode.getParentNode).getOrElse(domNode), gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
      if (fittingNode.isEmpty) {
        failAndReportMisfit()
      }
      else {
        fittingNodes = fittingNode.get :: fittingNodes
        //when proceeding to the next definition, old misfits do not matter anymore
        misfitHolder.wipe()
      }
    })
  }

  def elementDoesNotFit(domNode: DomNode): Unit = {
    definition.theSeq.foreach(gaugeElement => {
      val fit = nodeFits(Option(domNode.getParentNode).getOrElse(domNode), gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
      if (fit.isDefined) {
        fail(s"Current element matches the provided gauge, although expected not to!\n Gauge spec: $gaugeElement\n Fitting node: ${fit.get.domNode.prettyString()} found")
      }
      //when proceeding to the next definition, old misfits do not matter anymore
      misfitHolder.wipe()
    })
  }

  private def verifyClassesOnCandidate(domNode: DomNode, elem: Elem, misfitRelevance: MisfitRelevance): Boolean = {
    val definitionAttributes = elem.attributes.asAttrMap

    def assertContainsClass(domNode: DomNode, clazz: String): Boolean = {
      val domNodeClass = domNode.getAttributes.getNamedItem("class")
      if (domNodeClass == null) {
        misfitHolder.addMisfit(Misfit(misfitRelevance, s"Expected element to contain the class $clazz, but didn't contain any class attribute in ${domNode.prettyString()}"))
        false
      } else {
        val containsClass = domNodeClass.getNodeValue.split(" ").map(_.trim).filter(_.nonEmpty).contains(clazz)
        if (!containsClass) {
          misfitHolder.addMisfit(Misfit(misfitRelevance, s"Expected element to contain the class $clazz, but only contained ${domNodeClass.getNodeValue} in ${domNode.prettyString()}"))
        }
        containsClass
      }
    }

    definitionAttributes.get("class").map(
      _.split(" ")
        .map(_.trim)
        .filter(_.nonEmpty)
        .map(clazz => assertContainsClass(domNode, clazz)
        ).forall(identity)
    ).forall(identity)
  }

  private def nodeToCssSelector(node: Node) = {
    def classSelector = {
      val classAttribute = node.attribute("class")
      classAttribute match {
        case Some(classes) => classes.head.toString().split(" ").filter(_.nonEmpty).fold("")(_ + "." + _)
        case None => ""
      }
    }

    node.label + classSelector
  }

  private def nodeFits(node: DomNode, gaugeDefinition: Seq[Node], previousSibling: Option[DomNode], misfitRelevance: MisfitRelevance): Option[Fit] = {
    val definitionIt = gaugeDefinition.iterator
    var nodeFits = true
    var firstFittingSubNode: Option[Fit] = None
    var previousNode = previousSibling
    var nodeMisfitRelevance = misfitRelevance

    while (nodeFits && definitionIt.hasNext) {
      nodeMisfitRelevance += 1
      nodeFits = definitionIt.next() match {
        case nodeDef: Elem =>
          val fit = findFittingNode(node, nodeDef, previousNode, nodeMisfitRelevance)
          if (fit.isDefined) {
            previousNode = fit.map(_.domNode)
            nodeMisfitRelevance = fit.get.misfitRelevance
            if (firstFittingSubNode.isEmpty) {
              firstFittingSubNode = fit
            }
            true
          }
          else {
            false
          }
        case textDef: Text =>
          textFits(node, textDef.text.trim, previousNode, nodeMisfitRelevance)
        case atom: Atom[_] =>
          textFits(node, atom.text.trim, previousNode, nodeMisfitRelevance)
        case _ => true //everything except text and elements is ignored
      }
    }
    if (nodeFits) {
      if (node.getNodeName == "body" && firstFittingSubNode.isDefined) {
        firstFittingSubNode
      }
      else {
        Some(Fit(node, nodeMisfitRelevance))
      }
    } else None
  }

  private def textFits(nodeExpectedToContainText: DomNode, expectedText: String, previousSibling: Option[DomNode], misfitRelevance: MisfitRelevance): Boolean = {
    if (expectedText.isEmpty) {
      true
    }
    else {
      def elementFits: Boolean = {
        def textElement = {
          val children = nodeExpectedToContainText.getChildNodes.asScala.toList
          val considerableNodes = previousSibling match {
            case Some(p) => children.filter(p.before(_))
            case None => children
          }
          considerableNodes.find(_.isInstanceOf[DomText])
        }

        textElement match {
          case Some(_) => Matchers.textMatches(expectedText, CandidateElement(misfitRelevance, textElement.get)) match {
            case None => true
            case Some(m) => misfitHolder.addMisfit(m); false
          }
          case None =>
            previousSibling match {
              case Some(p) => misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting Element: element [" + nodeExpectedToContainText.prettyString() + "] did not contain a text element after [" + p.prettyString() + "]"))
              case None => misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting Element: element [" + nodeExpectedToContainText.prettyString() + "] did not contain a text"))
            }
            false
        }
      }

      elementFits
    }
  }

  private def findFittingNode(currentNode: DomNode, gaugeElement: Elem, previousSibling: Option[DomNode], misfitRelevance: MisfitRelevance): Option[Fit] = {
    val cssSelector: String = nodeToCssSelector(gaugeElement)
    //search nodes below current Node in document that is being checked
    val candidates = currentNode.querySelectorAll(cssSelector)
    val candidatesIt = candidates.iterator()
    var fit: Option[Fit] = None

    if (!candidatesIt.hasNext) {
      misfitHolder.addMisfit(misfitRelevance, "Misfitting Element: No element matching cssSelector [" + cssSelector + "] found below " + currentNode.prettyString)
    }
    //test all candidate elements, whether they match given attribute expectations
    while (fit.isEmpty && candidatesIt.hasNext) {
      val candidate = candidatesIt.next()
      fit = verifyCandidate(candidate, gaugeElement, previousSibling, misfitRelevance)
    }
    fit
  }

  private def verifyCandidate(candidate: DomNode, gaugeElement: Elem, previousSibling: Option[DomNode], misfitRelevance: MisfitRelevance): Option[Fit] = {
    val definitionAttributes = gaugeElement.attributes.asAttrMap

    var matchesAllAttributes = true
    var attributeMisfitRelevance = misfitRelevance

    //verify if the current candidate matches all attribute expectations
    for (defAttr <- definitionAttributes
         if matchesAllAttributes) {
      val expectedKey = defAttr._1
      val expectedValue = defAttr._2.trim

      //don't test for class attribute, as it is used in the CSS selector to find candidates
      if (expectedKey != "class" && expectedValue.nonEmpty) {
        attributeMisfitRelevance += 1

        def matchesAttribute(): Boolean = {
          val attributes: dom.NamedNodeMap = candidate.getAttributes
          if (attributes == null) {
            misfitHolder.addMisfit(attributeMisfitRelevance, "Misfitting Element: element [" + candidate.prettyString() + "] does not have any attributes, but attribute [" + defAttr + "] expected")
            return false
          }
          val attr: dom.Node = attributes.getNamedItem(expectedKey)
          if (attr == null) {
            misfitHolder.addMisfit(attributeMisfitRelevance, "Misfitting Element: element [" + candidate.prettyString() + "] does not have the expected attribute [" + defAttr + "]")
            return false
          }
          Matchers.attributeMatches(expectedValue, CandidateAttribute(attributeMisfitRelevance, candidate, attr)) match {
            case None => true
            case Some(m) => misfitHolder.addMisfit(m); false
          }
        }

        matchesAllAttributes &= matchesAttribute()
      }
    }
    //if the current element matches all attributes and is in correct order, we will test child elements
    if (matchesAllAttributes && elementOrderCorrect(candidate, previousSibling, gaugeElement, attributeMisfitRelevance)) {
      val noPreviousSibling = None
      nodeFits(candidate, gaugeElement.child, noPreviousSibling, attributeMisfitRelevance + 1)
    } else {
      None
    }
  }

  private def failAndReportMisfit() = {
    val relevantMisfitsReason = misfitHolder.relevantMisfits().reverse.map(_.reason).mkString("\n")
    fail(relevantMisfitsReason + "\nCurrent document does not match provided gauge:\n" + definition.toString)
  }

  private def elementOrderCorrect(current: DomNode, previousSibling: Option[DomNode], gaugeElement: Elem, misfitRelevance: MisfitRelevance): Boolean = {
    if (previousSibling.isDefined && (current before previousSibling.get)) {
      misfitHolder.addMisfit(misfitRelevance, "Misfitting Element Order: Found [" + current.prettyString() + "\n] when looking for an element matching [\n" + gaugeElement + "\n]. It didn't fit, because it was found before [" + previousSibling.get.prettyString() + "\n] but was expected after it.")
      false
    }
    else true
  }

  implicit class OrderableDomNode(domNode: DomNode) {
    def before(other: DomNode): Boolean = {
      val following = org.w3c.dom.Node.DOCUMENT_POSITION_FOLLOWING
      val order = domNode.compareDocumentPosition(other)
      (order & following) == following
    }

    def contains(other: DomNode): Boolean = {
      val containedBy = org.w3c.dom.Node.DOCUMENT_POSITION_CONTAINED_BY
      val order = domNode.compareDocumentPosition(other)
      (order & containedBy) == containedBy
    }
  }

  implicit class PrettyPrintDomNode(domNode: DomNode) {
    def prettyString(): String = prettyString(0)

    private def prettyString(depth: Int): String = {
      def indention: String = {
        "\n" + (0 to depth).map(_ => " ").fold("")(_ + _)
      }

      domNode match {
        case t: DomText => t.asText()
        case e: DomElement =>
          indention + s"<${e.getNodeName + e.getAttributesMap.values().asScala.map(e => e.getNodeName + "=\"" + e.getNodeValue + "\"").fold("")(_ + " " + _)}>" +
            e.getChildren.asScala.map(_.prettyString(depth + 1)).fold("")(_ + _) +
            indention + s"</${e.getNodeName}>"
        case _ => ""
      }
    }
  }

}

@deprecated("Please switch to the HtmlGauge object or extend the HtmlGauge trait. The Gauge object will be removed in a future version, for clear disambiguation between HtmlGauge and JsonGauge", "ScalaWebTest 1.1.0")
object Gauge extends HtmlGauge
object HtmlGauge extends HtmlGauge

trait HtmlGauge {
  /**
    * Assert that the current document `fits` the HTML snippet provided as definition for the `Gauge`.
    *
    * Technically the HTML snippet has to be XML. You might have to add some closing tags for this. This XML is then used to create an abstract syntax tree (AST),
    * which is used as gauge definition.
    *
    * ==Overview==
    * For example:
    *
    * {{{
    * fits (<table class="zebra"><tr><td><a href="/path/to/node">something</a></td></tr></table>)
    * }}}
    *
    * The current document is expected to `fit` the provided `gauge` definition (the NodeSeq/XML parameter of the `fits` method).
    * The current document may contain additional elements, before the section which `fits` the `gauge`. The document may contain additional elements in between the one, resembled in the `gauge`.
    * Without special markers attributes and text, after being trimmed, have to be equal to the once in the `gauge`. The only exception is the class attribute.
    * The classes listed in the gauge, are expected to be available on the test node, but order as well as additional classes do not matter.
    *
    * The parent child relationships, as well as the order between the elements has to be the same, as in the `gauge`.
    *
    * You may use the following refinements:
    *
    * ==Markers==
    * =\u0040contains=
    * The marker `\u0040contains` might be used in [[scala.xml.Text]] and attributes of [[scala.xml.Elem]] to request that the text or attribute contains the following text, i.e.
    *
    * {{{
    * <a href="@contains index">Home</a>
    * }}}
    *
    * matches
    *
    * {{{
    *   <a href="/index.html">Home</a>
    * }}}
    *
    * but doesn't match
    *
    * {{{
    *   <a href="/home.html">Home</a>
    * }}}
    *
    * or
    *
    * {{{
    * <p>@contains new NEBA version available</p>
    * }}}
    *
    * matches
    *
    * {{{
    *  <p>new NEBA version available since</p>
    * }}}
    *
    * but doesn't match
    *
    * {{{
    *  <p>new neba version available since</p>
    * }}}
    *
    * =\u0040regex=
    * The marker `\u0040regex` might be used in [[scala.xml.Text]] and attributes of [[scala.xml.Elem]] to request a regex pattern match, i.e.
    *
    * {{{
    * <a href="@regex http:\/\/[a-zA-Z]+\.domain.com.*,"></a>
    * }}}
    *
    * matches
    *
    * {{{
    *   <a href="http://my.domain.com/index.html"></a>
    * }}}
    *
    * but doesn't match
    *
    * {{{
    *   <a href="http://my.domain.org/index.html"></a>
    * }}}
    *
    * or
    *
    * {{{
    * <p>@regex new NEBA version [0-9.]+ available</p>
    * }}}
    *
    * matches
    *
    * {{{
    *   <p>new NEBA Version 4.0.0 available</p>
    * }}}
    *
    * but doesn't match
    *
    * {{{
    *   <p>new NEBA Version 4.0.0-RC1 available</p>
    * }}}
    *
    *
    */
  def fits(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
    new Gauge(definition).fits()
  }

  /**
    * Synonym for [[Gauge.elementFits]]. Use whatever reads better in your current context.
    */
  def fit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = fits(definition)

  implicit class NotFit(notWord: NotWord) {
    /**
      * Synonym for [[doesnt.fit]]. Use whatever reads better in your current context.
      */
    def fit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = doesnt.fit(definition)
  }

  object doesnt {
    /**
      * Assert that the current document `doesnt fit` the html snippet provided as definition for the `Gauge`
      *
      * To get detailed information about available options in `Gauge` definitions, read the ScalaDoc of [[Gauge.elementFits]]
      */
    def fit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      new Gauge(definition).doesNotFit()
    }
  }

}
