package org.scalawebtest.integration.doc._009

import org.scalatest.AppendedClues
import org.scalawebtest.core.IntegrationFlatSpec
import org.scalawebtest.core.gauge.HtmlElementGauge
import dotty.xml.interpolator.*

class ElementGaugeSpec extends IntegrationFlatSpec with HtmlElementGauge with AppendedClues {
  config.useBaseUri("http://localhost:9090")
  path = "/galleryOverview.jsp"

  val imageGauge =
    xml"""
      <div class="columns image_columns">
        <a>
          <figure class="obj_aspect_ratio">
            <noscript>
              <img class="obj_full"></img>
            </noscript>
            <img class="obj_full lazyload" data-sizes="auto"></img>
          </figure>
        </a>
      </div>
    """

  "The gallery" should "contain the expected HTML for every image" in {
    def images = findAll(CssSelectorQuery("ul div.image_columns"))

    images.size should be > 5 withClue " - gallery didn't contain the expected amount of images"

    for (image <- images) {
      image fits imageGauge
    }
  }
}
