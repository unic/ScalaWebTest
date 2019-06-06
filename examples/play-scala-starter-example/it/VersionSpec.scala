class VersionSpec extends BaseSpec {
  path = "/"

  val playVersion = "2.8.0-M1"
  "The HomePage" should s"report play version $playVersion" in {
    fits(
      <div id="content">
        <blockquote>
          <p>You’re using Play {playVersion} abc</p>
        </blockquote>
      </div>
      )
  }
}