package org.scalawebtest.core.browser.webstorage

import java.util

import com.gargoylesoftware.htmlunit.{Page, StorageHolder}
import org.openqa.selenium.html5.{LocalStorage, SessionStorage}

class HtmlUnitLocalStorage(storageHolder: StorageHolder, storageType: StorageHolder.Type, page: Page) extends StorageHolderBasedWebStorage(storageHolder = storageHolder, storageType = storageType, page = page) with LocalStorage

class HtmlUnitSessionStorage(storageHolder: StorageHolder, storageType: StorageHolder.Type, page: Page) extends StorageHolderBasedWebStorage(storageHolder = storageHolder, storageType = storageType, page = page) with SessionStorage

abstract class StorageHolderBasedWebStorage(storageHolder: StorageHolder, storageType: StorageHolder.Type, page: Page) {

  def getItem(key: String): String = storageHolder.getStore(storageType, page).get(key)

  def keySet: util.Set[String] = storageHolder.getStore(storageType, page).keySet()

  def setItem(key: String, value: String): Unit = storageHolder.getStore(storageType, page).put(key, value)

  def removeItem(key: String): String = storageHolder.getStore(storageType, page).remove(key)

  def clear(): Unit = storageHolder.getStore(storageType, page).clear()

  def size: Int = storageHolder.getStore(storageType, page).size()
}
