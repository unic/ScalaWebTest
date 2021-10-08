package org.scalawebtest.integration.gauge

import org.scalawebtest.integration.ScalaWebTestBaseSpec
import dotty.xml.interpolator.*

class LoginSpec extends ScalaWebTestBaseSpec{
  path = "/protectedContent.jsp"

  "When accessing protectedContent it" should "show the login form" in {
     fits(
       xml"""
         <form name="login_form">
          <input name="username"></input>
          <input name="password"></input>
        </form>
       """)
  }
  it should "hide the protected content, when not logged in" in {
    not fit xml"""<p>sensitive information</p>"""
  }
  it should "show the protected content, after logging in" in {
    textField("username").value = "admin"
    pwdField("password").value = "secret"

    submit()

    fits(xml"""<p>sensitive information</p>""")
  }
}
