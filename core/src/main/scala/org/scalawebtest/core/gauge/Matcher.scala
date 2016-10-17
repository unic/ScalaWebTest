package org.scalawebtest.core.gauge

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
