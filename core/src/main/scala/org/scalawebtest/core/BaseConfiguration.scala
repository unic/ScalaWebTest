package org.scalawebtest.core

class BaseConfiguration() {
  var configurations: Map[String, WebClientExposingDriver => Unit] = Map()

  //initialize with sensible default configuration
  disableJavaScript()
  swallowJavaScriptErrors()
  disableCss()

  /**
    * Enable JavaScript evaluation in the webDriver
    * and choose whether to throw on JavaScript error
    */
  def enableJavaScript(throwOnError: Boolean): Unit = {
    configurations += "enableJavaScript" ->
      ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setJavaScriptEnabled(true))
    configurations += "throwOnJSError" ->
      ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(throwOnError))
  }

  /**
    * Disable JavaScript evaluation as well as throwing on JavaScript error in the webDriver
    */
  def disableJavaScript(): Unit = {
    configurations += "enableJavaScript" -> ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setJavaScriptEnabled(false))
    configurations += "throwOnJSError" -> ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(false))
  }

  /**
    * Throw on JavaScript Error. Preferably use [[BaseConfiguration.disableJavaScript()]], as the two configurations only make sense when combined.
    */
  def throwOnJavaScriptError(): Unit = configurations += "throwOnJSError" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(true))

  /**
    * Silently swallow JavaScript errors. Preferably use [[BaseConfiguration.disableJavaScript()]], as the two configurations only make sense when combined.
    */
  def swallowJavaScriptErrors(): Unit = configurations += "throwOnJSError" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setThrowExceptionOnFailingStatusCode(false))

  /**
    * Enable CSS evaluation in the webDriver.
    */
  def enableCss(): Unit = configurations += "enableCss" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setCssEnabled(true))

  /**
    * Disable CSS evaluation in the webDriver.
    */
  def disableCss(): Unit = configurations += "enableCss" ->
    ((webDriver: WebClientExposingDriver) => webDriver.getOptions.setCssEnabled(false))

}

class LoginConfiguration extends BaseConfiguration
class Configuration extends BaseConfiguration {
  //initialize with sensible default configuration
  var navigateToEnabled = true
  var navigateToEnforced = false

  /**
    * Enabling navigateTo is the default. [[org.scalatest.BeforeAndAfterEach.beforeEach]] Test navigateTo is called with the current value of [[IntegrationSpec.path]]
    */
  def enableNavigateTo(): Unit = navigateToEnabled = true

  /**
    * Disables the navigateTo call, which otherwise happens in [[org.scalatest.BeforeAndAfterEach.beforeEach]]
    */
  def disableNavigateTo(): Unit = navigateToEnabled = false

  /**
    * By default navigateTo only navigates to the configured path, if it isn't the same as the currentPage.
    * By enabling enforceNavigateTo, [[org.scalatest.selenium.WebBrowser.goTo()]] is always called.
    */
  def enforceNavigateTo(): Unit = navigateToEnforced = true

  /**
    * This is the default behavior. NavigateTo only calls [[org.scalatest.selenium.WebBrowser.goTo()]], if the
    * currentPage isn't the same as the configured path.
    */
  def doNotEnforceNavigateTo(): Unit = navigateToEnforced = false
}
