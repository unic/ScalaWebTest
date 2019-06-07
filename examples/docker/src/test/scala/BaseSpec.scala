import org.scalawebtest.core.gauge.HtmlGauge
import org.scalawebtest.core.{IntegrationFlatSpec, IntegrationFreeSpec}

abstract class BaseSpec extends IntegrationFlatSpec with HtmlGauge {
  config.useBaseUri("https://scalawebtest.org")
}
