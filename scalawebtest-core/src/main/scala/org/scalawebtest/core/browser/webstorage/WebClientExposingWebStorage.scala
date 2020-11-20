package org.scalawebtest.core.browser.webstorage

import java.util

import com.gargoylesoftware.htmlunit.{Page, StorageHolder}
import org.scalawebtest.core.browser.webstorage.common.WebStorageAccessor

class WebClientExposingWebStorage(storageHolder: StorageHolder, storageType:StorageHolder.Type, page: Page) extends WebStorageAccessor {

  override def getItem(key: String): String = storageHolder.getStore(storageType, page).get(key)

  override def keySet: util.Set[String] = storageHolder.getStore(storageType, page).keySet()

  override def setItem(key: String, value: String): Unit = storageHolder.getStore(storageType, page).put(key,value)

  override def removeItem(key: String): String = storageHolder.getStore(storageType, page).remove(key)

  override def clear(): Unit = storageHolder.getStore(storageType, page).clear()

  override def size: Int = storageHolder.getStore(storageType, page).size()
}
