package org.scalawebtest.core.browser.webstorage.common

import java.util

trait WebStorageAccessor {

  def getItem(key: String): String

  def keySet: util.Set[String]

  def setItem(key: String, value: String): Unit

  def removeItem(key: String): String

  def clear(): Unit

  def size: Int
}