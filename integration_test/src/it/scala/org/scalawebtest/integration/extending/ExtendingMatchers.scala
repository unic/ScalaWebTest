package org.scalawebtest.integration.extending

import org.scalawebtest.core.gauge.Gauge.{doesnt, fits}
import org.scalawebtest.core.gauge._
import org.scalawebtest.core.gauge.Matchers.{AttributeMatcher, TextMatcher}
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class ExtendingMatchers extends ScalaWebTestBaseSpec {

  //define your own TextMatcher and/or AttributeMatcher
  object EmailMatcher extends TextMatcher with AttributeMatcher {
    val emailRegexCore = "[A-Za-z]+[A-Za-z0-9._%-]?@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"

    override def textMatches(expected: String, element: CandidateElement) = {
      val emailRegex = s"^$emailRegexCore$$"

      if (element.text().matches(emailRegex)) {
        None
      } else {
        Some(Misfit(element.relevance, "Misfitting Text: The [" + element.text() + "] from [" + element.element + "] is not an email address"))
      }
    }

    override def attributeMatches(expected: String, attribute: CandidateAttribute) = {
      val emailOrMailToRegex = s"^(mailto:)?$emailRegexCore$$"

      if (attribute.value().matches(emailOrMailToRegex)) {
        None
      } else {
        Some(Misfit(attribute.relevance, "Misfitting Attribute: [" + attribute.name() + "] in [" + attribute.containingElement + "] with value[" + attribute.value() + "] is not an email address or mailto: link"))
      }
    }

    override val marker: String = "@email"
  }

  //prepend your matcher to the list of textMatchers and/or attributeMatchers
  Matchers.textMatchers = EmailMatcher :: Matchers.textMatchers
  Matchers.attributeMatchers = EmailMatcher :: Matchers.attributeMatchers

  //use your custom matcher
  path = "/contact.jsp"
  "Contact page" should "contain a valid email" in {
    fits(<a class="mail" href="@email">@email</a>)
  }
  it should "not have an email address in the web link" in {
    doesnt fit <a class="web" href="@email"></a>
  }
  it should "not have an email address in the web link text" in {
    doesnt fit <a class="web">@email</a>
  }
}
