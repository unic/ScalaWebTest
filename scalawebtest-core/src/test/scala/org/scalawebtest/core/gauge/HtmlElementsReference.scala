package org.scalawebtest.core.gauge

/**
  * Contains all HTML Elements that ever existed (in the body)
  * according to https://developer.mozilla.org/en-US/docs/Web/HTML/Element
  */
object HtmlElementsReference {
  val contentSectioning = List(
    "address",
    "article",
    "aside",
    "footer",
    "header",
    "h1",
    "h2",
    "h3",
    "h4",
    "h5",
    "h6",
    "hgroup",
    "main",
    "nav",
    "section")

  val textContent = List(
    "blockquote",
    "dd",
    "dir",
    "div",
    "dl",
    "dt",
    "figcaption",
    "figure",
    "hr",
    "li",
    "main",
    "ol",
    "p",
    "pre",
    "ul")

  val inlineTextSemantics = List(
    "a",
    "abbr",
    "b",
    "bdi",
    "bdo",
    "br",
    "cite",
    "code",
    "data",
    "dfn",
    "em",
    "i",
    "kbd",
    "mark",
    "q",
    "rb",
    "rp",
    "rt",
    "rtc",
    "ruby",
    "s",
    "samp",
    "small",
    "span",
    "strong",
    "sub",
    "sup",
    "time",
    "tt",
    "u",
    "var",
    "wbr")

  val imageAndMultimedia = List(
    "area",
    "audio",
    "img",
    "map",
    "track",
    "video")

  val embeddedContent = List(
    "applet",
    "embed",
    "iframe",
    "noembed",
    "object",
    "param",
    "picture",
    "source")

  val scripting = List(
    "canvas",
    "noscript",
    "script")

  val demarcatingEdits = List(
    "del",
    "ins")

  val tableContent = List(
    "caption",
    "col",
    "colgroup",
    "table",
    "tbody",
    "td",
    "tfoot",
    "th",
    "thead",
    "tr")

  val forms = List(
    "button",
    "datalist",
    "fieldset",
    "form",
    "input",
    "label",
    "legend",
    "meter",
    "optgroup",
    "option",
    "output",
    "progress",
    "select",
    "textarea")

  val interactiveElements = List(
    "details",
    "dialog",
    "menu",
    "menuitem",
    "summary")

  val webComponents = List(
    "content",
    "element",
    "shadow",
    "slot",
    "template")

  val obsoleteAndDeprecated = List(
    "acronym",
    "applet",
    "basefont",
    "bgsound",
    "big",
    "blink",
    "center",
    "command",
    "content",
    "dir",
    "element",
    "font",
    "frame",
    "frameset",
    "image",
//    "isindex", only existed in head
    "keygen",
    "listing",
    "marquee",
    "menuitem",
    "multicol",
    "nextid",
    "nobr",
    "noembed",
    "noframes",
    "plaintext",
    "shadow",
    "spacer",
    "strike",
    "tt",
    "xmp")

  val bodyElements =
    contentSectioning ::: textContent ::: inlineTextSemantics ::: imageAndMultimedia :::
      embeddedContent ::: scripting ::: demarcatingEdits ::: tableContent ::: forms :::
      interactiveElements ::: webComponents

  val bodyElementsIncludingDeprecated = bodyElements ::: obsoleteAndDeprecated


}
