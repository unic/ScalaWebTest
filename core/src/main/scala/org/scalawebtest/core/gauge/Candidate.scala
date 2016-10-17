package org.scalawebtest.core.gauge

import com.gargoylesoftware.htmlunit.html.DomNode
import org.w3c.dom

case class CandidateAttribute(relevance: Int, containingElement: DomNode, attribute: dom.Node) {
  def value() = attribute.getNodeValue

  def name() = attribute.getNodeName
}

case class CandidateElement(relevance: Int, element: DomNode) {
  def text() = element.asText()
}
