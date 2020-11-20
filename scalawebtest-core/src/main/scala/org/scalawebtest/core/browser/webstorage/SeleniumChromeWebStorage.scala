package org.scalawebtest.core.browser.webstorage

import java.util

import org.openqa.selenium.html5.{LocalStorage, SessionStorage}
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor

class SeleniumChromeLocalStorage(localStorage: LocalStorage) extends WebStorageAccessor {

  override def getItem(key: String): String = localStorage.getItem(key)

  override def keySet: util.Set[String] = localStorage.keySet()

  override def setItem(key: String, value: String): Unit = localStorage.setItem(key, value)

  override def removeItem(key: String): String = localStorage.removeItem(key)

  override def clear(): Unit = localStorage.clear()

  override def size: Int = localStorage.size()
}

class SeleniumChromeSessionStorage(sessionStorage: SessionStorage) extends WebStorageAccessor {
  override def getItem(key: String): String = sessionStorage.getItem(key)

  override def keySet: util.Set[String] = sessionStorage.keySet()

  override def setItem(key: String, value: String): Unit = sessionStorage.setItem(key,value)

  override def removeItem(key: String): String = sessionStorage.removeItem(key)

  override def clear(): Unit = sessionStorage.clear()

  override def size: Int = sessionStorage.size()
}
