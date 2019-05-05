/*
 * Copyright 2019 the original author or authors.
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

import org.slf4j.{Logger, LoggerFactory}

trait Configurable {

  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def configFor(name: String): Option[String] = {
    val sysProp = System.getProperty(name)
    if (sysProp != null) {
      logger.info(s"System property $name found with value $sysProp")
      Some(sysProp)
    } else {
      logger.info(s"No system property $name found, deferring to environment variables")

      val envVar = System.getenv(name)
      if (envVar != null) {
        logger.info(s"Environment variable $name found with value $envVar")
        Some(envVar)
      }
      else {
        logger.info(s"No environment variable $name found")
        None
      }
    }
  }

  def requiredConfigFor(name: String): String = {
    configFor(name) match {
      case Some(s) => s
      case None =>
        val ex = new RuntimeException(s"Required configuration for $name missing. Provide a value via system property or environment variable.")
        logger.error(s"Required configuration for $name missing.", ex)
        throw ex
    }
  }
}
