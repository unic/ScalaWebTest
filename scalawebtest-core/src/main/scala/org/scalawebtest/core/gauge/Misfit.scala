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
package org.scalawebtest.core.gauge

import scala.io.AnsiColor._

case class Misfit(relevance: Int, topic: String, detail: String, expected: Option[String], actual: Option[String]) {
  def message: String = {
    val expectActualMessage =
      (
        for {
          e <- expected
          a <- actual
        }
          yield
            s"""
               |$BLUE${UNDERLINED}Expected:$RESET\n
               |$BLUE$e$RESET\n
               |$RED${UNDERLINED}Actual:$RESET\n
               |$RED$a$RESET
               |""".stripMargin
        ).getOrElse("")

    s"""
       |$RED$UNDERLINED$topic:$RESET\n
       |$RED$detail$RESET
       |$expectActualMessage
       |""".stripMargin
  }
}

class MisfitHolder {
  var misfits: List[Misfit] = Nil

  def addMisfitByValues(relevance: Int, topic: String, detail: String, expected: Option[String], actual: Option[String]): Unit = {
    misfits = Misfit(relevance, topic, detail, expected, actual) :: misfits
  }

  def addMisfit(misfit: Misfit): Unit = {
    misfits = misfit :: misfits
  }

  def mostRelevant: Int = misfits.map(_.relevance).max

  def relevantMisfits: List[Misfit] = misfits.filter(_.relevance == mostRelevant)

  def wipe(): Unit = misfits = Nil
}
