import org.scalawebtest.core.gauge.HtmlGauge
import org.scalawebtest.core.{IntegrationFlatSpec, IntegrationFreeSpec}

abstract class BaseSpec extends IntegrationFlatSpec with HtmlGauge {
  override val host = "http://localhost:9000"
}