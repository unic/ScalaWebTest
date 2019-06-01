/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalawebtest.core

/**
  * Default settings
  */
trait IntegrationSettings {
  @deprecated(message =
    """
      |The 'host' property was moved/merged into the 'baseUri' in the Configuration class.
      |Configuration holds the configurations for the test execution.
      |If you used 'host = "http://localhost:8181"' and 'projectRoot = "/web-app"' before, then use 'config.useBaseUri("http://locahost:8181/web-app")' instead.
      |If you used 'host = "http://localhost:8181"' and 'loginPath = "/login"' before, then use 'loginConfig.useBaseUri("http://locahost:8181/login")' instead.
    """.stripMargin, since = "ScalaWebTest 3.0.0")
   final val host: String = "The IntegrationSettings.host property was deprecated with ScalaWebTest 3.0.0 - use config.useBaseUri and loginConfig.useUri instead"

  @deprecated(message =
    """
      |The 'host' property was moved/merged into the 'baseUri' in the Configuration class.
      |Configuration holds the configurations for the test execution.
      |If you used 'host = "http://localhost:8181"' and 'projectRoot = "/web-app"' before, then use 'config.useBaseUri("http://locahost:8181/web-app")' instead.
    """.stripMargin, since = "ScalaWebTest 3.0.0")
  final val projectRoot: String = "The IntegrationSettings.projectRoot property was deprecated with ScalaWebTest 3.0.0 - use config.useBaseUri instead"
}
