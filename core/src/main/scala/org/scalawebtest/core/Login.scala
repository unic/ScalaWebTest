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
package org.scalawebtest.core

trait Login {
  val username = "admin"
  val password = "admin"
}

trait FormBasedLogin extends Login { self: IntegrationSpec =>
  val username_fieldname = "j_username"
  val password_fieldname = "j_password"

  val loginPath = "/libs/granite/core/content/login.html"

  override def login() = {
    withoutCss(withoutFollowingRedirects[Any](Any => {
      eventually(loginTimeout)({
        go to s"$host$loginPath"
        click on username_fieldname
        emailField(username_fieldname).value = username
        click on password_fieldname
        pwdField(password_fieldname).value = password

        submit()
      })
    }))(())
  }
}

trait BasicAuthLogin extends Login { self: IntegrationSpec =>

}

trait CookieBasedLogin extends Login { self: IntegrationSpec =>

}
