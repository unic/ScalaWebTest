package org.scalawebtest.integration.json

import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.Json

//Can only be used in specs which request /jsonResponse.json.jsp
trait FitsTypeMismatchBehavior {
  self: ScalaWebTestJsonBaseSpec =>

  def jsonGaugeCompletelyFitting(gaugeType: GaugeType): Unit = jsonGaugeFitting(gaugeType, completely = true)

  def jsonGaugeFitting(gaugeType: GaugeType, completely: Boolean = false): Unit = {
    def dijkstra = Json.parse(webDriver.getPageSource)

    def dijkstraFits(gaugeType: GaugeType) = {
      if (completely)
        dijkstra completelyFits gaugeType
      else
        dijkstra fits gaugeType
    }

    "When verifying JSON using fitsTypes or fitsTypesAndArraySizes" should
      "fail when a String is expected, but an Int provided" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"yearOfBirth": ""}"""
      }
    }
    it should "fail when an Int is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"name": 0}"""
      }
    }
    it should "fail when a Boolean is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"name": true}"""
      }
    }
    it should "fail when an Object is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"name": {}}"""
      }
    }
    it should "fail when an Array is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"name": []}"""
      }
    }
    it should "fail when null is expected, but a String provided" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"name": null}"""
      }
    }
    it should "fail when a property is missing" in {
      assertThrows[TestFailedException] {
        dijkstraFits(gaugeType) of """{"thesis": "Communication with an Automatic Computer"}"""
      }
    }
  }
}
