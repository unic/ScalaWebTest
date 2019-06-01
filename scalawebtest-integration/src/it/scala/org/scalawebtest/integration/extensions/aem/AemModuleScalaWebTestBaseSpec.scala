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
package org.scalawebtest.integration.extensions.aem

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.SpanSugar._
import org.scalawebtest.aem.{AemTweaks, AuthorLogin}
import org.scalawebtest.core.{FormBasedLogin, IntegrationFlatSpec}

import scala.language.postfixOps

abstract class AemModuleScalaWebTestBaseSpec extends IntegrationFlatSpec with FormBasedLogin with AemTweaks with AuthorLogin {
  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/fakeLogin.jsp")

  override def loginTimeout = Timeout(5 seconds)
}
