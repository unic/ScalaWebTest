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

import java.util.Base64

import org.scalatest.ConfigMap
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.SpanSugar._
import org.slf4j.LoggerFactory

import scala.language.postfixOps

/**
  * Extend this trait when implementing your own login implementation,
  * to assert identical username, password field names across all implementations.
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
  private val logger = LoggerFactory.getLogger(getClass.getName)

  val username_fieldname = "j_username"
  val password_fieldname = "j_password"

  def loginTimeout: Timeout = Timeout(5 seconds)

  override def login(configMap: ConfigMap): Unit = {
    eventually(loginTimeout)({
      logger.info(s"Going to ${loginConfig.loginUri}")
      go to loginConfig.loginUri.toString
      logger.info(s"Filling login form at ${loginConfig.loginUri}")
      click on username_fieldname
      emailField(username_fieldname).underlying.sendKeys(username)
      click on password_fieldname
      pwdField(password_fieldname).underlying.sendKeys(password)

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
  def base64Encode(value: String): String = Base64.getEncoder.encodeToString(value.getBytes())

  asWebClientExposingDriverOrError(webDriver)
    .getClient
    .addRequestHeader("Authorization", "Basic " + base64Encode(username + ":" + password))
}
