package org.scalawebtest.core.gauge

import org.jsoup.nodes.{Element => JElement, Node => JNode, TextNode => JTextNode}
import scala.jdk.CollectionConverters._

object JNodePrettifier {
  implicit class PrettyStringProvider(domNode: JNode) {
  def prettyString: String = prettyString(0) + "\n"

  private def prettyString(depth: Int): String = {
    def indention: String = {
      "\n" + (0 to depth).map(_ => "  ").mkString
    }

    domNode match {
      case t: JTextNode => t.text.trim
      case e: JElement =>
        indention + s"<${e.nodeName}${e.attributes}>" +
          e.childNodes.asScala.map(_.prettyString(depth + 1)).mkString +
          (if (e.children.size > 0) indention else "") + s"</${e.nodeName}>"
      case o => o.toString
    }
  }
}
}


