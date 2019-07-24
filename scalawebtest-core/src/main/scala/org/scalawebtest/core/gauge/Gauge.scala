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

import org.jsoup.Jsoup
import org.jsoup.nodes.{Element => JElement, Node => JNode, TextNode => JTextNode}
import org.openqa.selenium.WebDriver
import org.scalatest.Assertions
import org.scalatest.words.NotWord
import org.scalawebtest.core.gauge.JNodePrettifier.PrettyStringProvider

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._
import scala.io.AnsiColor.{UNDERLINED, RESET}
import scala.xml._

/**
  * Gauge provides functions to write integration tests with very low effort. For a detailed description of it's usage,
  * see [[org.scalawebtest.core.gauge.HtmlGauge.fits HtmlGauge.fits]] and [[org.scalawebtest.core.gauge.HtmlGauge.doesnt.fit HtmlGauge.doesnt.fit]]
  * as well as [[org.scalawebtest.core.gauge.HtmlElementGauge.GaugeFromElement.fits GaugeFromElement.fits]] and [[org.scalawebtest.core.gauge.HtmlElementGauge.GaugeFromElement#doesntFit GaugeFromElement.doesntFit]]
  */
class Gauge(definition: NodeSeq)(implicit webDriver: WebDriver) extends Assertions {
  type MisfitRelevance = Int
  val MISFIT_RELEVANCE_START_VALUE: MisfitRelevance = 0
  val misfitHolder = new MisfitHolder

  case class Fit(domNode: JNode, misfitRelevance: MisfitRelevance)

  def fits(): Unit = {
    fitsCurrentPageSource(webDriver.getPageSource)
  }

  def fitsCurrentPageSource(currentPageSource: String): Unit = {
     var fittingNodes = List[Fit]()

    val currentPage = Jsoup.parse(currentPageSource)
    definition.theSeq.foreach(gaugeElement => {
      val fittingNode = nodeFits(currentPage, gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
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
    doesNotFitCurrentPageSource(webDriver.getPageSource)
  }

  def doesNotFitCurrentPageSource(currentPageSource: String): Unit = {
    val currentPage = Jsoup.parse(currentPageSource)
    definition.theSeq.foreach(gaugeElement => {
      val fit = nodeFits(currentPage, gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
      if (fit.isDefined) {
        fail(s"Current document matches the provided gauge, although expected not to!\n Gauge spec: [$gaugeElement]\n Fitting node: [${fit.get.domNode.prettyString}] found")
      }
      //when proceeding to the next definition, old misfits do not matter anymore
      misfitHolder.wipe()
    })
  }

  def elementFits(domNode: JElement): Unit = {
    var fittingNodes = List[Fit]()
    definition.theSeq.foreach(gaugeElement => {
      val fittingNode = nodeFits(Option(domNode.parent).getOrElse(domNode), gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
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

  def elementDoesNotFit(domNode: JElement): Unit = {
    definition.theSeq.foreach(gaugeElement => {
      val fit = nodeFits(Option(domNode.parent).getOrElse(domNode), gaugeElement, None, MISFIT_RELEVANCE_START_VALUE)
      if (fit.isDefined) {
        fail(s"Current element matches the provided gauge, although expected not to!\n Gauge spec: [$gaugeElement]\n Fitting node: [${fit.get.domNode.prettyString}] found")
      }
      //when proceeding to the next definition, old misfits do not matter anymore
      misfitHolder.wipe()
    })
  }

  private def verifyClassesOnCandidate(domNode: JNode, elem: Elem, misfitRelevance: MisfitRelevance): Boolean = {
    val definitionAttributes = elem.attributes.asAttrMap

    def assertContainsClass(domNode: JNode, clazz: String): Boolean = {
      val domNodeClass = domNode.attributes.get("class")
      if (domNodeClass == null) {
        misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting Element", s"Expected element to contain the class [$clazz], but didn't contain any class attribute in [${domNode.prettyString}]", Some(clazz), Some("")))
        false
      } else {
        val containsClass = domNodeClass.split(" ").map(_.trim).filter(_.nonEmpty).contains(clazz)
        if (!containsClass) {
          misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting Element", s"Expected element to contain the class [$clazz], but only contained [$domNodeClass] in [${domNode.prettyString}]", Some(clazz), Some(domNodeClass)))
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
        case Some(classes) =>
          classes.headOption
            .map(_.toString.split(" +"))
            .filter(_.nonEmpty)
            .map(_.mkString(".", ".", ""))
            .getOrElse("")
        case _ => ""
      }
    }

    node.label + classSelector
  }

  private def nodeFits(node: JElement, gaugeDefinition: Seq[Node], previousSibling: Option[JNode], misfitRelevance: MisfitRelevance): Option[Fit] = {

    /**
      * When using variables in gauge definitions, the variables are represented using Atom.
      * Therefore
      * {{{<p>I have {bananas.size} Bananas <b> yeah</b></p>}}}
      * is an Element with the following children:
      * {{{Text(I have), Atom(3), Text(Bananas ), Elem(b)}}}
      * This code will merge them to:
      * {{{Text(I have 3 Bananas ), Elem(b)}}}
      */
    def mergeTextAndAtom(l: List[Node]): List[Node] = {
      @tailrec
      def recMerge(l: List[Node], acc: List[Node]): List[Node] = {
        l match {
          case Nil => acc
          case e :: n =>
            e match {
              case a: Atom[_] => acc.headOption match {
                case Some(Text(at)) => recMerge(n, Text(at + a.text) :: acc.tail)
                case _ => recMerge(n, Text(a.text) :: acc)
              }
              case Text(t) =>
                acc.headOption match {
                  case Some(Text(at)) => recMerge(n, Text(at + t) :: acc.tail)
                  case _ => recMerge(n, Text(t) :: acc)
                }
              case _ =>
                recMerge(n, e :: acc)
            }
        }
      }

      recMerge(l, Nil).reverse
    }

    val definitionIt = mergeTextAndAtom(gaugeDefinition.toList).iterator
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
        case _ => true //everything except text and elements is ignored
      }
    }
    if (nodeFits) {
      if (node.nodeName == "body" && firstFittingSubNode.isDefined) {
        firstFittingSubNode
      }
      else {
        Some(Fit(node, nodeMisfitRelevance))
      }
    } else None
  }

  private def textFits(nodeExpectedToContainText: JNode, expectedText: String, previousSibling: Option[JNode], misfitRelevance: MisfitRelevance): Boolean = {
    if (expectedText.isEmpty) {
      true
    }
    else {
      def elementFits: Boolean = {
        def textElement = {
          val children = nodeExpectedToContainText.childNodes.asScala.toList
          val considerableNodes = previousSibling match {
            case Some(p) => children.filter(p.isBefore(_))
            case None => children
          }
          considerableNodes.find(_.isInstanceOf[JTextNode]).map(_.asInstanceOf[JTextNode])
        }

        textElement match {
          case Some(text) => Matchers.textMatches(expectedText, CandidateElement(misfitRelevance, text)) match {
            case None => true
            case Some(m) => misfitHolder.addMisfit(m); false
          }
          case None =>
            previousSibling match {
              case Some(p) => misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting Element", s"element [${nodeExpectedToContainText.prettyString}] did not contain a text element after [${p.prettyString}]", None, None))
              case None => misfitHolder.addMisfit(Misfit(misfitRelevance, "Misfitting Element", s"element [${nodeExpectedToContainText.prettyString}] did not contain a text", None, None))
            }
            false
        }
      }

      elementFits
    }
  }

  private def findFittingNode(currentNode: JElement, gaugeElement: Elem, previousSibling: Option[JNode], misfitRelevance: MisfitRelevance): Option[Fit] = {
    val cssSelector: String = nodeToCssSelector(gaugeElement)
    //search nodes below current Node in document that is being checked
    val candidates = currentNode.select(cssSelector)
    val candidatesIt = candidates.iterator()
    var fit: Option[Fit] = None

    if (!candidatesIt.hasNext) {
      misfitHolder.addMisfitByValues(misfitRelevance, "Misfitting Element", s"No element matching cssSelector [$cssSelector] found below ${currentNode.prettyString}", None, None)
    }
    //test all candidate elements, whether they match given attribute expectations
    while (fit.isEmpty && candidatesIt.hasNext) {
      val candidate = candidatesIt.next()
      fit = verifyCandidate(candidate, gaugeElement, previousSibling, misfitRelevance)
    }
    fit
  }

  private def verifyCandidate(candidate: JElement, gaugeElement: Elem, previousSibling: Option[JNode], misfitRelevance: MisfitRelevance): Option[Fit] = {
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

        def matchesAttribute: Boolean = {
          val attributes = candidate.attributes
          if (attributes == null) {
            misfitHolder.addMisfitByValues(attributeMisfitRelevance, "Misfitting Element", s"element [${candidate.prettyString}] does not have any attributes, but attribute [$expectedKey] expected with value [$expectedValue]", Some(expectedKey), Some(""))
            return false
          }
          val attr = attributes.get(expectedKey)
          if (attr == null) {
            misfitHolder.addMisfitByValues(attributeMisfitRelevance, "Misfitting Element", s"element [${candidate.prettyString}] does not have the expected attribute [$expectedKey] expected with value [$expectedValue]", Some(expectedKey), Some(attributes.asList.asScala.map(_.getKey).mkString(" ")))
            return false
          }
          Matchers.attributeMatches(expectedValue, CandidateAttribute(attributeMisfitRelevance, candidate, expectedKey, attr)) match {
            case None => true
            case Some(m) => misfitHolder.addMisfit(m); false
          }
        }

        matchesAllAttributes &= matchesAttribute
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
    val misfitSeparator = ("\u035F" * 80) + RESET + "\n"
    val reportSeparator = "\n" + UNDERLINED + misfitSeparator
    def misfitReasons(misfits: List[Misfit]) = misfits.reverse.map(_.message).mkString(reportSeparator, misfitSeparator, reportSeparator)

    if (misfitHolder.relevantMisfits.size > 10) {
      fail(s"Too many Misfits, only showing the first 5 of ${misfitHolder.relevantMisfits.size} Misfits!\n" +
        misfitReasons(misfitHolder.relevantMisfits.take(5)) +
        s"\n\n... ${misfitHolder.relevantMisfits.size - 5} Misfits truncated!" +
        s"\nCurrent document does not match provided gauge:\n${definition.toString}")
    } else {
      fail(s"${misfitReasons(misfitHolder.relevantMisfits)}\n\nCurrent document does not match provided gauge:\n${definition.toString}")
    }
  }

  private def elementOrderCorrect(current: JNode, previousSibling: Option[JNode], gaugeElement: Elem, misfitRelevance: MisfitRelevance): Boolean =
    previousSibling match {
      case Some(prev) if current.isBefore(prev) =>
        misfitHolder.addMisfitByValues(misfitRelevance, s"Misfitting Element Order", s"Found [${current.prettyString}] when looking for an element matching [$gaugeElement]. \nIt didn't fit, because it was found before [${previousSibling.get.prettyString}] but was expected after it.", None, None)
        false
      case _ =>
        true
    }

  implicit class OrderableJNode(domNode: JNode) {
    def isBefore(other: JNode): Boolean = {
      def parents(node: JNode): List[JNode] = {
        @tailrec
        def parentsInner(node: JNode, parents: List[JNode]): List[JNode] = {
          if (node.hasParent)
            parentsInner(node.parent, node :: parents)
          else
            parents
        }

        parentsInner(node, Nil).reverse
      }

      def nodeWithParentsBefore(self: List[JNode], others: List[JNode]): Boolean = {
        if (self.tail.headOption == others.tail.headOption) {
          self.head.siblingIndex < others.head.siblingIndex
        } else {
          nodeWithParentsBefore(self.tail, others.tail)
        }
      }

      val domNodeAndParents = parents(domNode)
      val otherAndParents = parents(other)

      val maxDepth = Math.min(domNodeAndParents.length, otherAndParents.length)

      def comparableSlice(l: List[JNode]) = l.slice(l.length - maxDepth, l.length)

      nodeWithParentsBefore(comparableSlice(domNodeAndParents), comparableSlice(otherAndParents))
    }
  }
}

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
    * * The marker `\u0040contains` might be used in [[scala.xml.Text$]] and attributes of [[scala.xml.Elem]] to request that the text or attribute contains the following text, i.e.
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
    * The marker `\u0040regex` might be used in [[scala.xml.Text$]] and attributes of [[scala.xml.Elem]] to request a regex pattern match, i.e.
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
  def fits(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = {
    new Gauge(definition).fits()
  }

  /**
    * Synonym for [[org.scalawebtest.core.gauge.HtmlGauge#fits]]. Use whatever reads better in your current context.
    */
  def fit(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = fits(definition)

  implicit class NotFit(notWord: NotWord) {
    /**
      * Synonym for [[org.scalawebtest.core.gauge.HtmlGauge.doesnt#fit]]. Use whatever reads better in your current context.
      */
    def fit(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = doesnt.fit(definition)
  }

  object doesnt {
    /**
      * Assert that the current document `doesnt fit` the html snippet provided as definition for the `Gauge`
      *
      * To get detailed information about available options in `Gauge` definitions, read the ScalaDoc of [[org.scalawebtest.core.gauge.HtmlGauge#fits HtmlGauge.fits]]
      */
    def fit(definition: NodeSeq)(implicit webDriver: WebDriver): Unit = {
      new Gauge(definition).doesNotFit()
    }
  }

}
