package org.scalawebtest.core.gauge

import java.net.{URI, URL}

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.{Matchers => STMatchers}
import org.scalatest.{AppendedClues, ConfigMap, OptionValues}
import org.scalawebtest.core.Configurable

class ConfigurableTest extends AnyFreeSpec with STMatchers with AppendedClues with OptionValues {

  object testee extends Configurable

  private val configs = Map(
    "validInt" -> "42",
    "validDouble" -> "123.456",
    "validString" -> "write integration tests",
    "validBoolean" -> "true",
    "validURL" -> "https://www.scalawebtest.org",
    "validURI" -> "https://www.scalawebtest.org"
  )

  val configMap = new ConfigMap(configs)

  "The Configurable should be able to read a" - {
    "String containing an Int as Int" in {
      testee.configFor[Int](configMap)("validInt").value shouldBe 42
      testee.requiredConfigFor[Int](configMap)("validInt") shouldBe 42
    }
    "String containing a Double as Double" in {
      testee.configFor[Double](configMap)("validDouble").value shouldBe 123.456
      testee.requiredConfigFor[Double](configMap)("validDouble") shouldBe 123.456
    }
    "String as String" in {
      testee.configFor[String](configMap)("validString").value shouldBe "write integration tests"
      testee.requiredConfigFor[String](configMap)("validString") shouldBe "write integration tests"
    }
    "String containing a Boolean as Boolean" in {
      testee.configFor[Boolean](configMap)("validBoolean").value shouldBe true
      testee.requiredConfigFor[Boolean](configMap)("validBoolean") shouldBe true
    }
    "String containing a URL as URL" in {
      testee.configFor[URL](configMap)("validURL").value shouldBe new URL("https://www.scalawebtest.org")
      testee.requiredConfigFor[URL](configMap)("validURL") shouldBe new URL("https://www.scalawebtest.org")
    }
    "String containing URI as URI" in {
      testee.configFor[URI](configMap)("validURL").value shouldBe new URI("https://www.scalawebtest.org")
      testee.requiredConfigFor[URI](configMap)("validURL") shouldBe new URI("https://www.scalawebtest.org")
    }
  }
  "The Configurable should be able to read any property as String" in {
    configs.foreachEntry({
      (key, value) => testee.configFor[String](configMap)(key).value shouldBe value
    })
  }
  "The Configurable should prevent reading properties of wrong Type" - {
    "String as Int" in {
      testee.configFor[Int](configMap)("validString") shouldBe None
    }
    "String as Double" in {
      testee.configFor[Double](configMap)("validString") shouldBe None
    }
    "String as Boolean" in {
      testee.configFor[Boolean](configMap)("validString") shouldBe None
    }
    "String as URL" in {
      testee.configFor[URL](configMap)("validString") shouldBe None
    }
    "String as URI" in {
      testee.configFor[URI](configMap)("validString") shouldBe None
    }
  }

  "The Configurable should throw an error when reading a required property of wrong Type" - {
    "String as Int" in {
      assertThrows[RuntimeException] {
        testee.requiredConfigFor[Int](configMap)("validString")
      }
    }
    "String as Double" in {
      assertThrows[RuntimeException] {
        testee.requiredConfigFor[Double](configMap)("validString")
      }
    }
    "String as Boolean" in {
      assertThrows[RuntimeException] {
        testee.requiredConfigFor[Boolean](configMap)("validString")
      }
    }
    "String as URL" in {
      assertThrows[RuntimeException] {
        testee.requiredConfigFor[URL](configMap)("validString")
      }
    }
    "String as URI" in {
      assertThrows[RuntimeException] {
        testee.requiredConfigFor[URI](configMap)("validString")
      }
    }
  }
}
