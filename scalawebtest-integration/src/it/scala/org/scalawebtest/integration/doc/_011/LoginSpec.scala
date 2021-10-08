package org.scalawebtest.integration.doc._011

import org.scalawebtest.core.IntegrationFlatSpec

import org.scalawebtest.core.gauge.HtmlGauge
import dotty.xml.interpolator.*

class LoginSpec extends IntegrationFlatSpec with HtmlGauge {
  config.useBaseUri("http://localhost:9090")
  path = "/protectedContent.jsp"

  "When accessing protectedContent it" should "show the login form" in {
    currentPage fits
      xml"""
        <form name="login_form">
          <input name="username"></input>
          <input name="password"></input>
        </form>
      """
  }

  it should "hide the protected content, when not logged in" in {
    currentPage doesNotFit xml"""<p>sensitive information</p>"""
  }

  it should "show the protected content, after logging in" in {
    textField("username").value = "admin"
    pwdField("password").value = "secret"

    submit()

    currentPage fits xml"""<p>sensitive information</p>"""
  }
}
