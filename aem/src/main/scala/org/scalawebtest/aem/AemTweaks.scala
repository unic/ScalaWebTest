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
package org.scalawebtest.aem

import org.scalawebtest.aem.WcmMode._
import org.scalawebtest.core._

/**
  * Extend this trait to inherit useful default configuration for AEM projects.
  *
  * In addition it allows for convenient selection of the wcmmode.
  */
trait AemTweaks {
  self: IntegrationSpec with FormBasedLogin =>

  override val config = new Configuration() with AemConfig
  override val loginPath = "/libs/granite/core/content/login.html"

  trait AemConfig {
    self: BaseConfiguration =>
    def setWcmMode(wcmMode: WcmMode) = configurations += "wcmMode" ->
      ((webDriver: WebClientExposingDriver) => setWcmModeCookie(wcmMode))
  }

  private def setWcmModeCookie(mode: WcmMode) {
    add cookie("wcmmode", mode.toString)
  }

  /**
    * Fixture to set the wccmode for the given function call
    */
  def withWcmMode[X](mode: WcmMode) = withWcmModeInternal(mode, _: X => Unit)

  private def withWcmModeInternal[X](mode: WcmMode, f: X => Unit): X => Unit = {
    x: X => {
      setWcmModeCookie(mode)
      try {
        f(x)
      } finally {
        setWcmModeCookie(DISABLED)
      }
    }
  }

}
