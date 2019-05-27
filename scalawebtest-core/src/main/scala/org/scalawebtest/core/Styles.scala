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

import org.scalatest._
import org.scalatest.refspec.RefSpec

/**
  * ScalaTest provides a wide variety of styles. To make the creation of your BaseTrait easier,
  * ScalaWebTest extends the needed combination of traits for you and provides style specific abstract classes to extend.
  */

abstract class IntegrationFunSuite extends FunSuiteBehavior with IntegrationSpec
abstract class FunSuiteBehavior extends FunSuite with Matchers with Inspectors

abstract class IntegrationFlatSpec extends FlatSpecBehavior with IntegrationSpec
abstract class FlatSpecBehavior extends FlatSpec with Matchers with Inspectors

abstract class IntegrationFunSpec extends FunSpecBehavior with IntegrationSpec
abstract class FunSpecBehavior extends FunSpec with Matchers with Inspectors

abstract class IntegrationWordSpec extends WordSpecBehavior with IntegrationSpec
abstract class WordSpecBehavior extends WordSpec with Matchers with Inspectors

abstract class IntegrationFreeSpec extends FreeSpecBehavior with IntegrationSpec
abstract class FreeSpecBehavior extends FreeSpec with Matchers with Inspectors

abstract class IntegrationPropSpec extends  PropSpecBehavior with IntegrationSpec
abstract class PropSpecBehavior extends PropSpec with Matchers with Inspectors

abstract class IntegrationFeatureSpec extends FeatureSpecBehavior with IntegrationSpec
abstract class FeatureSpecBehavior extends FeatureSpec with Matchers with Inspectors

abstract class IntegrationRefSpec extends RefSpecBehavior with IntegrationSpec
abstract class RefSpecBehavior extends RefSpec with Matchers with Inspectors

