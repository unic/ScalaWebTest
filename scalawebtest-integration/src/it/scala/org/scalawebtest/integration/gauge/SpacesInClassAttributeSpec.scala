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
package org.scalawebtest.integration.gauge

import org.scalawebtest.integration.ScalaWebTestBaseSpec

class SpacesInClassAttributeSpec extends ScalaWebTestBaseSpec {
  path = "/galleryOverview.jsp"

  "Spaces in the class attribute" should "be used to separate the classes" in {
    fit(<img class="obj_full lazyload"></img>)
  }
  it should "not be an issue if more then one space is used two separate two classes" in {
    fit(<img class="obj_full   lazyload"></img>)
  }
  it should "not be an issue if the class attribute is empty" in {
    fit(<img class=""></img>)
  }
  it should "not be an issue if the class attribute only contains spaces" in {
    fit(<img class="  "></img>)
  }
}
