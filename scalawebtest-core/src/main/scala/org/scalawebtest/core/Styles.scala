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

