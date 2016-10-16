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

import com.gargoylesoftware.htmlunit.html.{DomElement, DomNode, DomText}
import org.scalatest.Assertions
import org.scalatest.words.NotWord
import org.w3c._

import scala.collection.JavaConverters._
import scala.xml.{Elem, Node, NodeSeq, Text}

/**
  * Gauge provides functions to write integration tests with very low effort. For a detailed description of it's usage,
  * see [[org.scalawebtest.core.Gauge.fits]]
  * and [[org.scalawebtest.core.Gauge.doesnt.fit]]
  */
class Gauge(definition: NodeSeq)(implicit webDriver: WebClientExposingDriver) extends Assertions {
  val MISFIT_RELEVANCE_START_VALUE: Int = 0

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

  private def textFits(textElement: DomNode, expectedText: String, previousSibling: Option[DomNode], misfitRelevance: Int): Boolean = {
    if (expectedText.isEmpty) {
      true
    }
    else {
      def elementFits: Boolean = {
        Matcher.textMatches(expectedText, CandidateElement(misfitRelevance, textElement)) match {
          case None => true
          case Some(m) => MisfitHolder.addMisfit(m); false
        }
      }

      elementFits && textElementOrderCorrect(textElement, previousSibling, misfitRelevance)
    }
  }

  private def findFittingNode(currentNode: DomNode, gaugeElement: Elem, previousSibling: Option[DomNode], misfitRelevance: Int): Option[DomNode] = {
    val cssSelector: String = nodeToCssSelector(gaugeElement)
    //search nodes below current Node in document that is being checked
    val candidates = currentNode.querySelectorAll(cssSelector)
    val candidatesIt = candidates.iterator()
    var fittingNode: Option[DomNode] = None

    if (!candidatesIt.hasNext) {
      MisfitHolder.addMisfit(misfitRelevance, "Misfitting Element: No element matching cssSelector [" + cssSelector + "] found below " + currentNode.prettyString)
    }
    //test all candidate elements, whether they match given attribute expectations
    while (fittingNode.isEmpty && candidatesIt.hasNext) {
      val candidate = candidatesIt.next()
      val definitionAttributes = gaugeElement.attributes.asAttrMap

      var matchesAllAttributes = true
      var attributeMisfitRelevance = misfitRelevance

      //verify if the current candidate matches all attribute expectations
      for (defAttr <- definitionAttributes; if matchesAllAttributes) {
        val expectedKey = defAttr._1
        val expectedValue = defAttr._2.trim

        //don't test for class attribute, as it is used in the CSS selector to find candidates
        if (expectedKey != "class" && expectedValue.nonEmpty) {
          attributeMisfitRelevance += 1
          def matchesAttribute(): Boolean = {
            val attributes: dom.NamedNodeMap = candidate.getAttributes
            if (attributes == null) {
              MisfitHolder.addMisfit(attributeMisfitRelevance, "Misfitting Element: element [" + candidate.prettyString() + "] does not have any attributes, but attribute [" + defAttr + "] expected")
              return false
            }
            val attr: dom.Node = attributes.getNamedItem(expectedKey)
            if (attr == null) {
              MisfitHolder.addMisfit(attributeMisfitRelevance, "Misfitting Element: element [" + candidate.prettyString() + "] does not have the expected attribute [" + defAttr + "]")
              return false
            }
            Matcher.attributeMatches(expectedValue, CandidateAttribute(attributeMisfitRelevance, candidate, attr)) match {
              case None => true
              case Some(m) => MisfitHolder.addMisfit(m); false
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
    val relevantMisfitsReason = MisfitHolder.relevantMisfits().reverse.map(_.reason).mkString("\n")
    fail(relevantMisfitsReason + "\nCurrent document does not match provided checking gauge:\n" + definition.toString)
  }

  private def elementOrderCorrect(current: DomNode, previousSibling: Option[DomNode], misfitRelevance: Int): Boolean = {
    if (previousSibling.isDefined && (current before previousSibling.get)) {
      MisfitHolder.addMisfit(misfitRelevance, "Misfitting Element Order: Wrong order of elements [" + current.prettyString() + "\n] was found before [" + previousSibling.get.prettyString() + "\n] but expected the other way")
      false
    }
    else true
  }

  private def textElementOrderCorrect(current: DomNode, previousSibling: Option[DomNode], misfitRelevance: Int): Boolean = {
    if (previousSibling.isDefined && !(current contains previousSibling.get)) {
      MisfitHolder.addMisfit(misfitRelevance, "Misfitting Element Order: Wrong order of elements [" + current.prettyString() + "\n] should contain [" + previousSibling.get.prettyString() + "\n] but didn't")
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
    * =\u0040exact=
    * The marker `\u0040exact` might be used in [[scala.xml.Text]] and attributes of [[scala.xml.Elem]] to request an exact match, i.e.
    *
    * {{{
    * <form method="@exact POST"></form>
    * }}}
    *
    * or
    *
    * {{{
    * <p>@exact new NEBA version available</p>
    * }}}
    *
    * =\u0040regex=
    * The marker `\u0040regex` might be used in [[scala.xml.Text]] and attributes of [[scala.xml.Elem]] to request a regex pattern match, i.e.
    *
    * {{{
    * <a href="@regex http:\/\/[a-zA-Z]+\.domain.com.*,"></a>
    * }}}
    *
    * or
    *
    * {{{
    * <p>@regex new NEBA version [0-9.]+ available</p>
    * }}}
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

case class CandidateAttribute(relevance: Int, containingElement: DomNode, attribute: dom.Node) {
  def value() = attribute.getNodeValue

  def name() = attribute.getNodeName
}

case class CandidateElement(relevance: Int, element: DomNode) {
  def text() = element.asText()
}

/**
  * Verifies element text or attribute values with one of its PluggableMatchers.
  */
object Matcher {
  //Warning! Order within this lists matters. The first Matcher with a valid marker will be used. The marker of the DefaultMatcher is always valid.
  val textMatchers: List[TextMatcher] = ContainsMatcher :: RegexMatcher :: DefaultMatcher :: Nil
  val attributeMatchers: List[AttributeMatcher] = ContainsMatcher :: RegexMatcher :: DefaultMatcher :: Nil

  def attributeMatches(expectedValue: String, attribute: CandidateAttribute) = {
    val matcher = attributeMatchers.find(m => expectedValue.startsWith(m.marker))
    matcher match {
      case None => Some(noMatcherFoundMisfit(attribute.relevance, expectedValue, attributeMatchers))
      case Some(m) => m.attributeMatches(expectedValue.stripPrefix(m.marker), attribute)
    }
  }

  def textMatches(expectedValue: String, element: CandidateElement) = {
    val matcher = textMatchers.find(m => expectedValue.startsWith(m.marker))
    matcher match {
      case None => Some(noMatcherFoundMisfit(element.relevance, expectedValue, textMatchers))
      case Some(m) => m.textMatches(expectedValue.stripPrefix(m.marker), element)
    }
  }

  private def noMatcherFoundMisfit(relevance: Int, expectedValue: String, matchers: List[PluggableMatcher]): Misfit = {
    Misfit(relevance, "No matcher found for checkingGauge definition [" + expectedValue + "]. None of the registered matchers [" + matchers + "] contained a matching marked [" + matchers.map(_.marker) + "]")
  }

  /**
    * PlugableMatchers can be used to evaluate content of an element or attribute.
    * They are triggered by a marked, such as "@regex ".
    * To provide an additional matcher, extend a subclass of PlugableMatcher and add your matcher to the according matchers list in the Matcher object.
    */
  trait PluggableMatcher {
    val marker: String
  }

  trait TextMatcher extends PluggableMatcher {
    def textMatches(expected: String, element: CandidateElement): Option[Misfit]
  }

  trait AttributeMatcher extends PluggableMatcher {
    def attributeMatches(expected: String, attribute: CandidateAttribute): Option[Misfit]
  }

  object DefaultMatcher extends TextMatcher with AttributeMatcher {
    override val marker = ""

    override def attributeMatches(expected: String, attribute: CandidateAttribute) = {
      if (attribute.value().equals(expected)) {
        None
      } else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute: [" + attribute.name() + "] in [" + attribute.containingElement + "] with value[" + attribute.value() + "] didn't equal [" + expected + "]"))
      }
    }

    override def textMatches(expected: String, element: CandidateElement) = {
      if (element.text().trim().equals(expected)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text: The [" + element.text() + "] from [" + element.element + "] didn't equal [" + expected + "]"))
      }
    }
  }

  object RegexMatcher extends TextMatcher with AttributeMatcher {
    override val marker = "@regex "

    override def attributeMatches(expected: String, attribute: CandidateAttribute) = {
      if (attribute.value().matches(expected)) {
        None
      } else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute: [" + attribute.name() + "] in [" + attribute.containingElement + "] with value[" + attribute.value() + "] didn't match regex pattern [" + expected + "]"))
      }
    }

    override def textMatches(expected: String, element: CandidateElement) = {
      if (element.text().matches(expected)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text: The [" + element.text() + "] from [" + element.element + "] didn't match regex pattern [" + expected + "]"))
      }
    }
  }

  object ContainsMatcher extends TextMatcher with AttributeMatcher {
    override val marker = "@contains "

    override def attributeMatches(expected: String, attribute: CandidateAttribute) = {
      if (attribute.value().contains(expected)) {
        None
      }
      else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute: [" + attribute.name() + "] in [" + attribute.containingElement + "] with value[" + attribute.value() + "] didn't contain [" + expected + "]"))
      }
    }

    override def textMatches(expected: String, element: CandidateElement) = {
      if (element.text().contains(expected)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text: The [" + element.text + "] from [" + element.element + "] didn't contain [" + expected + "]"))
      }
    }
  }

}

case class Misfit(relevance: Int, reason: String)

object MisfitHolder {
  var misfits: List[Misfit] = Nil

  def addMisfit(relevance: Int, reason: String): Unit = {
    misfits = Misfit(relevance, reason) :: misfits
  }

  def addMisfit(misfit: Misfit): Unit = {
    misfits = misfit :: misfits
  }

  def mostRelevant() = misfits.map(_.relevance).max

  def relevantMisfits() = misfits.filter(_.relevance == mostRelevant())
}
