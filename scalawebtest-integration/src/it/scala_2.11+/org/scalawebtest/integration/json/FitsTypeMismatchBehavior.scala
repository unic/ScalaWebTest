package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import org.scalawebtest.integration.ScalaWebTestBaseSpec
import org.scalawebtest.json.JsonGaugeBuilder._
import play.api.libs.json.Json

//Can only be used in specs which request /jsonResponse.json.jsp
trait FitsTypeMismatchBehavior {
  self: ScalaWebTestBaseSpec =>

  def jsonGaugeFitting(gaugeType: GaugeType): Unit = {
    def dijkstra = Json.parse(webDriver.getPageSource)

    "When verifying JSON using fitsTypes or fitsTypesAndArraySizes" should
      "fail when a String is expected, but an Int provided" in {
      assertThrows[TestFailedException] {
        dijkstra fits gaugeType of """{"yearOfBirth": ""}"""
      }
    }
    it should "fail when an Int is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstra fits gaugeType of """{"name": 0}"""
      }
    }
    it should "fail when a Boolean is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstra fits gaugeType of """{"name": true}"""
      }
    }
    it should "fail when an Object is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstra fits gaugeType of """{"name": {}}"""
      }
    }
    it should "fail when an Array is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstra fits gaugeType of """{"name": []}"""
      }
    }
    it should "fail when a property is missing" in {
      assertThrows[TestFailedException] {
        dijkstra fits gaugeType of """{"thesis": "Communication with an Automatic Computer"}"""
      }
    }
  }
}
