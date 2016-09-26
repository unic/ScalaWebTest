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
package org.scalawebtest.integration

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalawebtest.aem.AemTweaks
import org.scalawebtest.core.{FormBasedLogin, IntegrationFlatSpec}
import org.scalatest.time.SpanSugar._

import scala.language.postfixOps

trait ScalaWebTestBaseSpec extends IntegrationFlatSpec with AemTweaks with FormBasedLogin{
  override val host = "http://localhost:8080"
  override val loginPath = "/fakeLogin.jsp"

  override val projectRoot = "/"

  override def loginTimeout = Timeout(5 seconds)
}
