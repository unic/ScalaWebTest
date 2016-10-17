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
import scala.xml.{Elem, Node, NodeSeq, Text}

/**
  * Gauge provides functions to write integration tests with very low effort. For a detailed description of it's usage,
  * see [[org.scalawebtest.core.gauge.Gauge.fits]]
  * and [[org.scalawebtest.core.gauge.Gauge.doesnt.fit]]
  */
class Gauge(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver) extends Assertions {
  val MISFIT_RELEVANCE_START_VALUE: Int = 0
  val misfitHolder = new MisfitHolder

  def fits(): Unit = {
    var fittingNodes = List[DomNode]()
    if (webDriver.getCurrentHtmlPage.isEmpty) fail("Current page is not of type HtmlPage. At the moment only Html documents are supported")
    val currentPage = webDriver.getCurrentHtmlPage.get
    var misfitRelevance = MISFIT_RELEVANCE_START_VALUE

    definition.theSeq.foreach(gaugeElement => {
      val fittingNode = nodeFits(currentPage.getDocumentElement, gaugeElement, None, misfitRelevance)
      if (fittingNode.isEmpty) {
        failAndReportMisfit()
      }
      else {
        fittingNodes = fittingNode.get :: fittingNodes
      }
      misfitRelevance += 1
    })
  }

  def doesNotFit(): Unit = {
    if (webDriver.getCurrentHtmlPage.isEmpty) fail("Current page is not of type HtmlPage. At the moment only Html documents are supported")
    val currentPage = webDriver.getCurrentHtmlPage.get
    var misfitRelevance = MISFIT_RELEVANCE_START_VALUE
    definition.theSeq.foreach(gaugeElement => {
      val fittingNode = nodeFits(currentPage.getDocumentElement, gaugeElement, None, misfitRelevance)
      if (fittingNode.isDefined) {
        fail("Current document matches the provided gauge, although expected not to!\n Fitting node " + fittingNode.head.prettyString() + " found")
      }
      misfitRelevance += 1
    })
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

  private def nodeFits(node: DomNode, gaugeDefinition: Seq[Node], previousSibling: Option[DomNode], misfitRelevance: Int): Option[DomNode] = {
    val definitionIt = gaugeDefinition.iterator
    var nodeFits = true
    var firstFittingSubNode: Option[DomNode] = None
    var previousNode = previousSibling

    while (nodeFits && definitionIt.hasNext) {
      nodeFits = definitionIt.next() match {
        case textDef: Text =>
          textFits(node, textDef.text.trim, previousNode, misfitRelevance + 1)
        case nodeDef: Elem =>
          val fittingDomNode = findFittingNode(node, nodeDef, previousNode, misfitRelevance + 1)
          if (fittingDomNode.isDefined) {
            previousNode = fittingDomNode
            if (firstFittingSubNode.isEmpty) {
              firstFittingSubNode = fittingDomNode
            }
            true
          }
          else {
            false
          }
        case _ => true //everything except text and elements is ignored
      }
    }
    if (nodeFits) {
      if (node.getNodeName == "body" && firstFittingSubNode.isDefined) {
        firstFittingSubNode
      }
      else {
        Some(node)
      }
    } else None
  }

  private def textFits(nodeExpectedToContainText: DomNode, expectedText: String, previousSibling: Option[DomNode], misfitRelevance: Int): Boolean = {
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
          case Some(e) => Matcher.textMatches(expectedText, CandidateElement(misfitRelevance, textElement.get)) match {
            case None => true
            case Some(m) => misfitHolder.addMisfit(m); false
          }
          case None =>
            previousSibling match {
              case Some(p) => misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting element: element [" + nodeExpectedToContainText.prettyString() + "] did not contain a text element after [" + p.prettyString() + "]"))
              case None => misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting element: element [" + nodeExpectedToContainText.prettyString() + "] did not contain a text"))
            }
            false
        }
      }
      elementFits
    }
  }

  private def findFittingNode(currentNode: DomNode, gaugeElement: Elem, previousSibling: Option[DomNode], misfitRelevance: Int): Option[DomNode] = {
    val cssSelector: String = nodeToCssSelector(gaugeElement)
    //search nodes below current Node in document that is being checked
    val candidates = currentNode.querySelectorAll(cssSelector)
    val candidatesIt = candidates.iterator()
    var fittingNode: Option[DomNode] = None

    if (!candidatesIt.hasNext) {
      misfitHolder.addMisfit(misfitRelevance, "Misfitting Element: No element matching cssSelector [" + cssSelector + "] found below " + currentNode.prettyString)
    }
    //test all candidate elements, whether they match given attribute expectations
    while (fittingNode.isEmpty && candidatesIt.hasNext) {
      val candidate = candidatesIt.next()
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
            Matcher.attributeMatches(expectedValue, CandidateAttribute(attributeMisfitRelevance, candidate, attr)) match {
              case None => true
              case Some(m) => misfitHolder.addMisfit(m); false
            }
          }
          matchesAllAttributes &= matchesAttribute()
        }
      }
      //if the current element matches all attributes and is in correct order, we will test child elements
      if (matchesAllAttributes && elementOrderCorrect(candidate, previousSibling, attributeMisfitRelevance)) {
        val noPreviousSibling = None
        fittingNode = nodeFits(candidate, gaugeElement.child, noPreviousSibling, attributeMisfitRelevance + 1)
      }
    }
    fittingNode
  }

  private def failAndReportMisfit() = {
    val relevantMisfitsReason = misfitHolder.relevantMisfits().reverse.map(_.reason).mkString("\n")
    fail(relevantMisfitsReason + "\nCurrent document does not match provided checking gauge:\n" + definition.toString)
  }

  private def elementOrderCorrect(current: DomNode, previousSibling: Option[DomNode], misfitRelevance: Int): Boolean = {
    if (previousSibling.isDefined && (current before previousSibling.get)) {
      misfitHolder.addMisfit(misfitRelevance, "Misfitting Element Order: Wrong order of elements [" + current.prettyString() + "\n] was found before [" + previousSibling.get.prettyString() + "\n] but expected the other way")
      false
    }
    else true
  }

  implicit class OrderableDomNode(domNode: DomNode) {
    def before(other: DomNode) = {
      val following = org.w3c.dom.Node.DOCUMENT_POSITION_FOLLOWING
      val order = domNode.compareDocumentPosition(other)
      (order & following) == following
    }

    def contains(other: DomNode) = {
      val containedBy = org.w3c.dom.Node.DOCUMENT_POSITION_CONTAINED_BY
      val order = domNode.compareDocumentPosition(other)
      (order & containedBy) == containedBy
    }
  }

  implicit class PrettyPrintDomNode(domNode: DomNode) {
    def prettyString(): String = prettyString(0)

    private def prettyString(depth: Int): String = {
      def indention: String = {
        "\n" + (0 to depth).map(x => " ").fold("")(_ + _)
      }

      domNode match {
        case t: DomText => t.asText()
        case e: DomElement =>

          indention + "<" + e.getNodeName + e.getAttributesMap.values().asScala.map(e => e.getNodeName + "=\"" + e.getNodeValue + "\"").fold("")(_ + " " + _) + ">" +
            e.getChildren.asScala.map(_.prettyString(depth + 1)).fold("")(_ + _) +
            indention + "</" + domNode.getNodeName + ">"
        case default => ""
      }
    }
  }

}

object Gauge {
  /**
    * Assert that the current document `fits` the html snippet provided as definition for the `Gauge`.
    *
    * ==Overview==
    * For example:
    *
    * {{{
    * fits (<table class="zebra"><tr><td><a href="/path/to/node">something</a></td></tr></table>)
    * }}}
    *
    * The provided XML is expected to match the current document, as loosely as possible. Without further markers everything is matched using contains methods.
    * The parent - child relationship is enforced, but missing siblings are ignored.
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
    * but doens't match
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
    * Synonym for [[Gauge.fits]]. Use whatever reads better in your current context.
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
      * To get detailed information about available options in `Gauge` definitions, read the ScalaDoc of [[Gauge.fits]]
      */
    def fit(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver): Unit = {
      new Gauge(definition).doesNotFit()
    }
  }

}
