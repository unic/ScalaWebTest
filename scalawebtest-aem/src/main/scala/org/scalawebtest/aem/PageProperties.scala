package org.scalawebtest.aem

import java.net.URI

import org.scalawebtest.core.IntegrationSpec
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.util.Try

/**
  * The PageProperties trait populates the pageProperties, and if applicable componentProperties and suffixProperties fields with a JsValue
  * representing the content of the currentPage, component and suffix respectively.
  * It does so by retrieving the JSON representation of the currentPage. This works by default on all CQ/AEM author instances.
  * In addition it provides convenience methods to access the pageProperties content.
  *
  * Only extend this trait in tests which need the feature, as it otherwise unnecessarily slows down your tests,
  * due to additional requests for page properties.
  */
trait PageProperties {
  self: IntegrationSpec with AemTweaks =>

  var pagePropertiesDepth = 10
  //we prefer null over Option because it is easier to access
  //and throwing an error is the expected behavior of a test in case of an unexpected situation
  var pageProperties: JsValue = _
  var componentProperties: JsValue = _
  var suffixProperties: JsValue = _

  /**
    * @return the property with the given name as String
    */
  def pageProperties(propertyName: String): String = (pageProperties \ propertyName).as[String]

  /**
    * @return the property from the jcr:content node, with the given name as String
    */
  def jcrContent(propertyName: String): String = (pageProperties \ "jcr:content" \ propertyName).as[String]

  /**
    * Provides additional convenience methods to access the pageProperties object
    */
  implicit class SearchableJsValue(container: JsValue) {

    /**
      * This is especially useful to find all components of a given
      * sling:resourceType within a parsys. For implementation details see [[findByProperty]]
      *
      * @return all values which are of the given sling:resourceType
      */
    def findByResourceType: String => Iterable[JsValue] = findByProperty("sling:resourceType")(_)

    /**
      * Tries to transform a JsValue to a JsObject. If possible, it searches through
      * the values of the object. It returns a list with all values, which are JsObjects
      * themselves and contain a property with the given name and value.
      *
      * This is especially useful to find all components, which match a certain criteria,
      * within a parsys.
      *
      * @return all values which contain a property with the given name and value
      */
    def findByProperty(name: String)(value: String): Iterable[JsValue] = {
      container match {
        case o: JsObject =>
          o.values
            .filter(_.isInstanceOf[JsObject])
            .filter(c => {
              (c \ name).toOption.exists(v => {
                val s = v.asOpt[String]
                s.contains(value)
              })
            })
        case _ => Nil
      }
    }

  }

  /**
    * Populates the [[org.scalawebtest.aem.PageProperties.pageProperties:play* pageProperties]] field with a [[play.api.libs.json.JsValue JsValue]], which represents the properties of the currentPage.
    * In case the [[org.scalawebtest.core.IntegrationSpec#uri uri]] points to something below jcr:content, the [[org.scalawebtest.aem.PageProperties#componentProperties componentProperties]] will be populate with the properties of the component,
    * and the [[org.scalawebtest.aem.PageProperties.pageProperties:play* pageProperties]] with those of the containing page.
    * In case the [[org.scalawebtest.core.IntegrationSpec#uri uri]] contains a suffix, the [[org.scalawebtest.aem.PageProperties#suffixProperties suffixProperties]] will be populated with the properties of the page referenced in the suffix.
    * It does so by manipulating the [[org.scalawebtest.core.IntegrationSpec#uri uri]] field, to request the JSON representation of the currentPage from CQ/AEM. This
    * feature is available on CQ/AEM author instances by default.
    * The enable.json property of the org.apache.sling.servlets.get.DefaultGetServlet of your CQ/AEM instance has to be set to true.
    */
  abstract override def beforeEach(): Unit = {
    val decomposedLink = new DecomposedLink(uri)

    def pagePropertiesUrl = s"${decomposedLink.protocolHostPort}${decomposedLink.pagePath}.$pagePropertiesDepth.json"

    def componentPropertiesUrl = {
      if (decomposedLink.pagePath != decomposedLink.path)
        Some(s"${decomposedLink.protocolHostPort}${decomposedLink.path}.$pagePropertiesDepth.json")
      else
        None
    }

    def suffixPropertiesUrl = {
      if (!decomposedLink.suffix.isEmpty)
        Some(s"${decomposedLink.protocolHostPort}${decomposedLink.suffix}.$pagePropertiesDepth.json")
      else
        None
    }

    if (config.navigateToBeforeEachEnabled) {
      pageProperties = Try {
        navigateToUri(pagePropertiesUrl)
        Json.parse(webDriver.getPageSource)
      }.toOption.orNull

      componentProperties = componentPropertiesUrl.flatMap(u =>
        Try {
          navigateToUri(u)
          Json.parse(webDriver.getPageSource)
        }.toOption
      ).orNull

      suffixProperties = suffixPropertiesUrl.flatMap(u =>
        Try {
          navigateToUri(u)
          Json.parse(webDriver.getPageSource)
        }.toOption
      ).orNull

      navigateToUri(url)
    }
  }

  private class DecomposedLink(uri: URI) {
    //AEM url structure protocol://host[:port]/path[/jcr:content/parsys/component][.selectors].extension[/suffix][#anchor][?requestParameters]
    //we are not interested in anchor and request parameters
    val protocolHostPort: String = s"${uri.getScheme}://${uri.getAuthority}"
    private val pathSelectorsExtensionSuffix = uri.getPath
    private val selectorsExtensionSuffix = substringAfter(pathSelectorsExtensionSuffix, ".")
    val suffix: String = selectorsExtensionSuffix.indexOf("/") match {
      case -1 => ""
      case i => selectorsExtensionSuffix.substring(i)
    }
    private val selectorsExtension = selectorsExtensionSuffix.stripSuffix(suffix)
    val extension: String = substringAfterLast(selectorsExtension, ".")
    val path: String = substringBefore(pathSelectorsExtensionSuffix, ".")
    val pagePath: String = substringBefore(path, "/_jcr_content")

    def substringAfter(s: String, k: String): String = {
      s.indexOf(k) match {
        case -1 => s
        case i => s.substring(i + k.length)
      }
    }

    def substringAfterLast(s: String, k: String): String = {
      s.lastIndexOf(k) match {
        case -1 => s
        case i => s.substring(i + k.length)
      }
    }

    def substringBefore(s: String, k: String): String = {
      s.indexOf(k) match {
        case -1 => s
        case i => s.substring(0, i)
      }
    }
  }

}
