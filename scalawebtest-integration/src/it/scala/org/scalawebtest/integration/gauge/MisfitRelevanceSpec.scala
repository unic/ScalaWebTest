package org.scalawebtest.integration.gauge

import org.scalatest.exceptions.TestFailedException
import org.scalawebtest.integration.{AdditionalAssertions, ScalaWebTestBaseSpec}

class MisfitRelevanceSpec extends ScalaWebTestBaseSpec with AdditionalAssertions {
  path = "/nested.jsp"
  "Misfit relevance" should "return the correct error message" in {
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
    )(message => {
      message should include("Misfitting Attribute:")
      message should include("[title] in [\n  <textarea title=\"hobbies\"></textarea>\n] with value [hobbies] didn't equal [hobby]")
      message should include regex "(?s)Expected:.*hobby.*Actual:.*hobbies"
    })
  }

}
