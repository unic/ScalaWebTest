package org.scalawebtest.core

class Configuration() {
  var configurations: Map[String, WebClientExposingDriver => Unit] = Map()

  //initialize with sensible default configuration
  disableJavaScript()
  swallowJavaScriptErrors()
  disableCss()

  def enableJavaScript(throwOnError: Boolean): Unit = {
    configurations += "enableJavaScript" ->
      ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setJavaScriptEnabled(true))
    configurations += "throwOnJSError" ->
      ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(throwOnError))
  }

  def disableJavaScript(): Unit = {
    configurations += "enableJavaScript" -> ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setJavaScriptEnabled(false))
    configurations += "throwOnJSError" -> ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(false))
  }

  def throwOnJavaScriptError(): Unit = configurations += "throwOnJSError" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(true))

  def swallowJavaScriptErrors(): Unit = configurations += "throwOnJSError" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(false))

  def enableCss(): Unit = configurations += "enableCss" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setCssEnabled(true))

  def disableCss(): Unit = configurations += "enableCss" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setCssEnabled(false))

}
