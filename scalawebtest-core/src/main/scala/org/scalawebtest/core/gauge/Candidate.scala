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

import com.gargoylesoftware.htmlunit.html.DomNode
import org.w3c.dom

case class CandidateAttribute(relevance: Int, containingElement: DomNode, attribute: dom.Node) {
  def value(): String = attribute.getNodeValue

  def name(): String = attribute.getNodeName
}

case class CandidateElement(relevance: Int, element: DomNode) {
  def text(): String = element.asText()
}
