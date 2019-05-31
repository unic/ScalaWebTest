package org.scalawebtest.integration.json

import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.json.JsonGauge

trait ScalaWebTestJsonBaseSpec extends IntegrationFlatSpec with JsonGauge{
    override val host = "http://localhost:9090"
    override val projectRoot = ""
}
