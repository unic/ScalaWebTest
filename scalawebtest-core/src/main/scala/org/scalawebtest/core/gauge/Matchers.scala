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

import org.scalawebtest.core.gauge.JNodePrettifier.PrettyStringProvider

/**
  * Verifies element text or attribute values with one of its PluggableMatchers.
  */
object Matchers {
  //Warning! Order within this lists matters. The first Matcher with a valid marker will be used. The marker of the DefaultMatcher is always valid.
  var textMatchers: List[TextMatcher] = ContainsMatcher :: RegexMatcher :: DefaultMatcher :: Nil
  var attributeMatchers: List[AttributeMatcher] = ContainsMatcher :: RegexMatcher :: DefaultMatcher :: Nil

  def attributeMatches(expectedValue: String, attribute: CandidateAttribute): Option[Misfit] = {
    val matcher = attributeMatchers.find(m => expectedValue.startsWith(m.marker))
    matcher match {
      case None => Some(noMatcherFoundMisfit(attribute.relevance, expectedValue, attributeMatchers))
      case Some(m) => m.attributeMatches(expectedValue.stripPrefix(m.marker), attribute)
    }
  }

  def textMatches(expectedValue: String, element: CandidateElement): Option[Misfit] = {
    val matcher = textMatchers.find(m => expectedValue.startsWith(m.marker))
    matcher match {
      case None => Some(noMatcherFoundMisfit(element.relevance, expectedValue, textMatchers))
      case Some(m) => m.textMatches(expectedValue.stripPrefix(m.marker), element)
    }
  }

  private def noMatcherFoundMisfit(relevance: Int, expectedValue: String, matchers: List[PluggableMatcher]): Misfit = {
    Misfit(relevance, "Matcher not found", "No matcher found for checkingGauge definition [" + expectedValue + "]. None of the registered matchers [" + matchers + "] contained a matching marked [" + matchers.map(_.marker) + "]", None, None)
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

    override def attributeMatches(expected: String, attribute: CandidateAttribute): Option[Misfit] = {
      if (attribute.value.equals(expected)) {
        None
      } else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute", s"[${attribute.name}] in [${attribute.containingElement.prettyString}] with value [${attribute.value}] didn't equal [$expected]", Some(expected), Some(attribute.value)))
      }
    }

    override def textMatches(expected: String, element: CandidateElement): Option[Misfit] = {
      if (element.text.trim.equals(expected)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text", s"The text [${element.text}] in [${element.element.parent.prettyString}] didn't equal [$expected]", Some(expected), Some(element.text)))
      }
    }
  }

  object RegexMatcher extends TextMatcher with AttributeMatcher {
    override val marker = "@regex "

    override def attributeMatches(expected: String, attribute: CandidateAttribute): Option[Misfit] = {
      if (attribute.value.matches(expected)) {
        None
      } else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute", s"The attribute [${attribute.name}] in [${attribute.containingElement.prettyString}] with value [${attribute.value}] didn't match regex pattern [$expected]", None, None))
      }
    }

    override def textMatches(expected: String, element: CandidateElement): Option[Misfit] = {
      if (element.text.trim().matches(expected)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text", s"The text [${element.text}] in [${element.element.parent.prettyString}] didn't match regex pattern [$expected]", None, None))
      }
    }
  }

  object ContainsMatcher extends TextMatcher with AttributeMatcher {
    override val marker = "@contains "

    override def attributeMatches(expected: String, attribute: CandidateAttribute): Option[Misfit] = {
      if (attribute.value.contains(expected)) {
        None
      }
      else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute", s"The attribute [${attribute.name}] in [${attribute.containingElement.prettyString}] with value [${attribute.value}] didn't contain [$expected]", Some(s"...$expected..."), Some(attribute.value)))
      }
    }

    override def textMatches(expected: String, element: CandidateElement): Option[Misfit] = {
      if (element.text.contains(expected)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text", s"The text [${element.text}] in [${element.element.parent.prettyString}] didn't contain [$expected]", Some(s"...expected..."), Some(element.text)))
      }
    }
  }

}
