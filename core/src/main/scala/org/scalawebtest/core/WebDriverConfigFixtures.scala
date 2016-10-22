package org.scalawebtest.core

/**
  * Provide fixtures to set a specific webdriver configuration for a given function call
  * and restore previous configuration after the function call.
  */
trait WebDriverConfigFixtures {
  self: IntegrationSpec =>

  /**
    * Do not follow redirects during the given function call.
    * Whatever setting was used for redirects, will be restored after this function call.
    */
  def withoutFollowingRedirects[X](f: X => Unit): X => Unit = withFollowingRedirectsInternal(f, enableRedirects = false)

  /**
    * Follow redirects during the given function call.
    * Whatever setting was used for redirects, will be restored after this function call.
    */
  def withFollowingRedirects[X](f: X => Unit): X => Unit = withFollowingRedirectsInternal(f, enableRedirects = true)

  /**
    * Disable CSS during the given function call.
    * Whatever setting was used with regard to CSS, will be restored after this function call.
    */
  def withoutCss[X](f: X => Unit): X => Unit = withCssInternal(f, enableCss = false)

  /**
    * Enable CSS during the given function call.
    * Whatever setting was used with regard to CSS, will be restored after this function call.
    */
  def withCss[X](f: X => Unit): X => Unit = withCssInternal(f, enableCss = true)

  /**
    * Disable JavaScript execution during the given function call.
    * Whatever setting was used for JavaScript execution, will be restored after this function call.
    */
  def withoutJavascript[X](f: X => Unit): X => Unit = withJavascript(f, enabled = false)

  /**
    * Enable JavaScript execution during the given function call.
    * Whatever setting was used for JavaScript execution, will be restored after this function call.
    */
  def withJavascript[X](f: X => Unit): X => Unit = withJavascript(f, enabled = true)

  private def withFollowingRedirectsInternal[X](f: (X) => Unit, enableRedirects: Boolean): (X) => Unit = {
    x: X => {
      val redirectionEnabled = webDriver.getOptions.isRedirectEnabled
      webDriver.getOptions.setRedirectEnabled(enableRedirects)
      try {
        f(x)
      } finally {
        webDriver.getOptions.setRedirectEnabled(redirectionEnabled)
      }
    }
  }

  private def withCssInternal[X](f: (X) => Unit, enableCss: Boolean): (X) => Unit = {
    x: X => {
      val wasEnabled = webDriver.getOptions.isCssEnabled
      webDriver.getOptions.setCssEnabled(enableCss)
      try {
        f(x)
      } finally {
        webDriver.getOptions.setCssEnabled(wasEnabled)
      }
    }
  }

  private def withJavascript[X](f: (X) => Unit, enabled: Boolean): (X) => Unit = {
    x: X => {
      val jsEnabled = webDriver.getOptions.isJavaScriptEnabled
      webDriver.getOptions.setJavaScriptEnabled(enabled)
      try {
        f(x)
      } finally {
        webDriver.getOptions.setJavaScriptEnabled(jsEnabled)
      }
    }
  }
}
