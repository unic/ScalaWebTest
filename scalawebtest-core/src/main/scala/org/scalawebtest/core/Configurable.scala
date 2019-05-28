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

import java.net.URL

import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

trait Configurable {

  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  case class Context(kind: String, name: String)

  trait Transformer[T] {
    def transform(s: String, c: Context): Option[T]
  }

  implicit object StringTransformer extends Transformer[String] {
    override def transform(s: String, c: Context): Option[String] = Some(s)
  }

  implicit object IntTransformer extends Transformer[Int] {
    override def transform(s: String, c: Context): Option[Int] = Try(s.toInt) match {
      case Success(i) => Some(i)
      case Failure(e) =>
        logger.error(s"Could not transform ${c.kind} ${c.name} with value $s to ${classOf[Int].getName}", e)
        None
    }
  }

  implicit object UrlTransformer extends Transformer[URL] {
    override def transform(s: String, c: Context): Option[URL] = Try(new URL(s)) match {
      case Success(i) => Some(i)
      case Failure(e) =>
        logger.error(s"Could not transform ${c.kind} ${c.name} with value $s to ${classOf[URL].getName}", e)
        None
    }
  }

  def configFor[T: Transformer](name: String): Option[T] = {
    def context(kind: String) = Context(kind, name)

    val sysProp = System.getProperty(name)
    if (sysProp != null) {
      logger.info(s"System property $name found with value $sysProp")
      implicitly[Transformer[T]].transform(sysProp, context("system property"))
    } else {
      logger.info(s"No system property $name found, deferring to environment variables")

      val envVar = System.getenv(name)
      if (envVar != null) {
        logger.info(s"Environment variable $name found with value $envVar")
        implicitly[Transformer[T]].transform(envVar, context("environment variable"))
      }
      else {
        logger.info(s"No environment variable $name found")
        None
      }
    }
  }

  def requiredConfigFor[T: Transformer](name: String): T = {
    configFor[T](name) match {
      case Some(s) => s
      case None =>
        val ex = new RuntimeException(s"Required configuration for $name missing. Provide a value via system property or environment variable.")
        logger.error(s"Required configuration for $name missing.", ex)
        throw ex
    }
  }
}
