package org.scalawebtest.integration.extensions.aem

import org.scalatest.time.{Seconds, Span}
import org.scalawebtest.aem.AemTweaks
import org.scalawebtest.aem.WcmMode.DISABLED
import org.scalawebtest.core.gauge.Gauge.fit
import org.scalawebtest.integration.ScalaWebTestBaseSpec

class AemTweaksSpec extends ScalaWebTestBaseSpec with AemTweaks {

  config.enableJavaScript(throwOnError = true)
  config.setWcmMode(DISABLED)

  path = "/cookieVisualizing.jsp"

  "When wcmmode DISABLED is select webBrowser" should "send the according cookie" in {
    implicitlyWait(Span(1, Seconds))
    eventually(timeout(Span(1, Seconds))) {
      fit(<li>@contains wcmmode</li>)
    }
  }

}
