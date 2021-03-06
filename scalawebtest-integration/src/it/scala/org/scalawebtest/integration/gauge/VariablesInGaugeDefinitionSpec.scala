package org.scalawebtest.integration.gauge

import org.scalawebtest.integration.ScalaWebTestBaseSpec

class VariablesInGaugeDefinitionSpec extends ScalaWebTestBaseSpec {
  path = "/navigation.jsp"

  "The text" should "match without a variable" in {
    fits(<ul><li><a>second navigation element</a></li></ul>)
  }
  it should "match with a variable in the beginning" in {
    val s = "second"
    fits(<ul><li><a>{s} navigation element</a></li></ul>)
  }
  it should "match with a variable at the end" in {
    val e = "element"
    fits(<ul><li><a>second navigation {e}</a></li></ul>)
  }
  it should "match with a variable in the middle" in {
    val n = "nav"
    fits(<ul><li><a>second {n}igation element</a></li></ul>)
  }

  "The attribute" should "match without a variable" in {
    fits(<ul><li><a href="/path/to/second/element"></a></li></ul>)
  }
  it should "match with a variable in the beginning" in {
    val p = "/path"
    fits(<ul><li><a href={p + "/to/second/element"}></a></li></ul>)
  }
  it should "match with a variable at the end" in {
    val e = "element"
    fits(<ul><li><a href={s"/path/to/second/$e"}></a></li></ul>)
  }
  it should "match with a variable in the middle" in {
    val s = "second"
    fits(<ul><li><a href={s"/path/to/$s/element"}></a></li></ul>)
  }
}

