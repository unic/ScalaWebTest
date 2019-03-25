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

import javax.xml.bind.DatatypeConverter

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.SpanSugar._

import scala.language.postfixOps

/**
  * Extend this trait when implementing you own login implementation,
  * to assert identical username, password configuration across all implementations
  */
trait Login {
  val username = "admin"
  val password = "admin"
}

/**
  * Extend the FormBasedLogin trait to provide login information via web form before every request.
  *
  * The following configurations are available with this login:
  *  - username
  *  - password
  *
  *  - usernameFieldName
  *  - passwordFieldName
  *  - loginPath
  *  - loginTimeout
  */
trait FormBasedLogin extends Login {
  self: IntegrationSpec =>
  val username_fieldname = "j_username"
  val password_fieldname = "j_password"

  @deprecated(message =
  """The 'loginPath' property was moved/merged into the 'baseURI' in the BaseConfiguration.
    |The BaseConfiguration is inherited by Configuration and LoginConfiguration and holds the configurations for test execution and login respectively.
    |If you used 'host = "http://localhost:8181"' and 'loginPath = "/login"' before, then use 'loginConfig.useBaseURI("http://locahost:8181/login")' instead.""".stripMargin,
    since = "ScalaWebTest 3.0.0")
  final val loginPath = "The FormBasedLogin.loginPath property was deprecated with ScalaWebTest 3.0.0 - use loginConfig.useBaseURI instead"

  def loginTimeout = Timeout(5 seconds)

  override def login(): Unit = {
    eventually(loginTimeout)({
      go to loginConfig.baseURI.toString
      click on username_fieldname
      emailField(username_fieldname).value = username
      click on password_fieldname
      pwdField(password_fieldname).value = password

      submit()
    })
  }
}

/**
  * Extend the BasicAuthLogin trait to provide credentials via basic authentication with every request.
  *
  * The following configurations are available with this login:
  *  - username
  *  - password
  */

trait BasicAuthLogin extends Login {
  self: IntegrationSpec =>
  def base64Encode(value: String): String = DatatypeConverter.printBase64Binary(value.getBytes())

  webDriver.getClient.addRequestHeader("Authorization", "Basic " + base64Encode(username + ":" + password))
}
