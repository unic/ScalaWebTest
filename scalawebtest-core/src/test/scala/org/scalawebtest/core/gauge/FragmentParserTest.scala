/*
 * Copyright 2019 the original author or authors.
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

import org.jsoup.nodes.{Node, TextNode}
import org.scalatest.{AppendedClues, FreeSpec, Matchers => STMatchers}

import scala.collection.JavaConverters._


class FragmentParserTest extends FreeSpec with STMatchers with AppendedClues {
  def renderElement(name: String): String = {
    def isSelfClosing: Boolean = List("br", "plaintext").contains(name)

    if (isSelfClosing) s"<$name/>" else s"<$name></$name>"
  }

  val emptyBreadcrumb = "fragment root"

  def expectedNode(name: String, expectedChildren: ((Node, String) => Unit)*)(self: Node, breadcrumb: String = emptyBreadcrumb): Unit = {
    self.nodeName() shouldBe name withClue s"$self in $breadcrumb should be be <$name>"
    self.childNodes() should have size expectedChildren.size withClue s"$self should contain ${expectedChildren.size} children"
    (self.childNodes.asScala zip expectedChildren).foreach {
      case (n, f) => f(n, if (breadcrumb == emptyBreadcrumb) s"$name" else s"$breadcrumb > $name")
    }
  }

  def expectedText(text: String)(self: Node, breadcrumb: String = emptyBreadcrumb): Unit = {
    self shouldBe a[TextNode] withClue s"$breadcrumb should contain a text"
    self.asInstanceOf[TextNode].getWholeText shouldBe text withClue s"$breadcrumb should contain the text $text"
  }

  "Parsing an HTML fragement should be successful for" - {
    "a single appearance of every HTML body element that ever existed" in {
      for (element <- HtmlElementsReference.bodyElementsIncludingDeprecated) {
        val parsed = FragmentParser.parseFragment(renderElement(element))
        parsed.size shouldBe 1 withClue s"Failed for $element"

        val expectedElementName = if (element == "image") "img" else element
        expectedNode(expectedElementName)(parsed.head)
      }
    }
    "fragments containing leading and trailing whitespace" in {
      val whitespaceFragment = """   <a href="/index.html">home</a>    """
      val parsed = FragmentParser.parseFragment(whitespaceFragment)

      expectedNode("a", expectedText("home"))(parsed.head)
    }
    "nested fragments should be correctly parsed to a tree" in {
      val parsed = FragmentParser.parseFragment("""<div><ul><li>first</li><li>second</li></ul></div>""")

      expectedNode("div",
        expectedNode("ul",
          expectedNode("li", expectedText("first")),
          expectedNode("li", expectedText("second"))
        )
      )(parsed.head)
    }
    "fragment starting with a custom element should be parsed the same as standard html elements" in {
      val customElement = """<popup-info img="img/alt.png" text="You can find your customer number on every invoice"></popup-info>"""

      val parsed = FragmentParser.parseFragment(customElement)

      expectedNode("popup-info")(parsed.head)
    }
  }
}

