package org.scalawebtest.integration.doc

import org.scalatest.AppendedClues
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.SpanSugar._
import org.scalawebtest.core.gauge.HtmlGauge
import org.scalawebtest.core.{FormBasedLogin, IntegrationFlatSpec}

import scala.language.postfixOps

trait MyProjectBaseSpec extends IntegrationFlatSpec with FormBasedLogin with AppendedClues with HtmlGauge {
  config.useBaseUri("http://localhost:9090")
  loginConfig.useLoginUri("http://localhost:9090/login.php")

  override def loginTimeout = Timeout(5 seconds)
}
