class HomePageSpec extends BaseSpec {
  path = "/"

  "The HomePage" should "contain a nice claim" in {
    fits(
      <div id="header">
        <span class="tag">write <strong>elenium</strong> and <strong>ScalaTest</strong> based integration tests in minutes</span>
      </div>
    )
  }
  it should "contain a succinct title" in {
    fits(<h1><a>Integration testing made easy</a></h1>) 
  }
}
