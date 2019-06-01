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
      |The 'host' property was moved/merged into the 'baseURI' in the BaseConfiguration.
      |The BaseConfiguration is inherited by Configuration and LoginConfiguration and holds the configurations for test execution and login respectively.
      |If you used 'host = "http://localhost:8181"' and 'projectRoot = "/web-app"' before, then use 'config.useBaseURI("http://locahost:8181/web-app")' instead.
      |If you used 'host = "http://localhost:8181"' and 'loginPath = "/login"' before, then use 'loginConfig.useBaseURI("http://locahost:8181/login")' instead.
    """.stripMargin, since = "ScalaWebTest 3.0.0")
   val host: String = "The IntegrationSettings.host property was deprecated with ScalaWebTest 3.0.0 - use config.useBaseURI and loginConfig.useBaseURI instead"

  @deprecated(message =
    """
      |The 'projectRoot' property was moved/merged into the 'baseURI' in the BaseConfiguration.
      |The BaseConfiguration is inherited by Configuration and LoginConfiguration and holds the configurations for test execution and login respectively.
      |If you used 'host = "http://localhost:8181"' and 'projectRoot = "/web-app"' before, then use 'config.useBaseURI("http://locahost:8181/web-app")' instead.
    """.stripMargin, since = "ScalaWebTest 3.0.0")
  final val projectRoot: String = "The IntegrationSettings.projectRoot property was deprecated with ScalaWebTest 3.0.0 - use config.useBaseURI instead"
}
