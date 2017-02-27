package org.scalawebtest.aem

import org.scalawebtest.core.{FormBasedLogin, IntegrationSpec}

/**
  * Extend this trait to get automatic login for tests running against an author instance.
  */
trait AuthorLogin extends FormBasedLogin {
  self: IntegrationSpec =>
  override val loginPath = "/libs/granite/core/content/login.html"
  override val username: String = "admin"
  override val password: String = "admin"
}

