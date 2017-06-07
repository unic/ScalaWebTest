package org.scalawebtest.integration.gauge

import org.scalatest.exceptions.TestFailedException
import org.scalawebtest.integration.{AdditionalAssertions, ScalaWebTestBaseSpec}

class MisfitRelevanceSpec extends ScalaWebTestBaseSpec with AdditionalAssertions{
  path = "/nested.jsp"
  "Misfit relevance" should "afa" in {
    assertThrowsAndTestMessage[TestFailedException](
        fits(<div>
          <select title="friendship book questions">
            <optgroup label="favorite color">
              <option value="green">green</option>
              <option value="red">red</option>
              <option value="blue">blue</option>
              <option value="yellow">yellow</option>
            </optgroup>
          </select>
          <textarea title="hobby"></textarea>
        </div>)
    )(message => message should startWith("Misfitting Attribute: [title] in [HtmlTextArea[<textarea title=\"hobbies\">]] with value[hobbies] didn't equal [hobby]"))
  }

}
