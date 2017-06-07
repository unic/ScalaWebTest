package org.scalawebtest.integration

import org.scalactic.source.Position
import org.scalatest.{Assertion, Succeeded, Suite}

import scala.reflect.ClassTag

trait AdditionalAssertions {self: Suite =>
    def assertThrowsAndTestMessage[T <: AnyRef](f: => Any)(messageTest: String => Assertion)(implicit classTag: ClassTag[T], pos: Position): Assertion = {
    val clazz = classTag.runtimeClass
    val threwExpectedException =
      try {
        f
        false
      }
      catch {
          case u: Throwable =>
            messageTest(u.getMessage)
            if (!clazz.isAssignableFrom(u.getClass)) {
              fail(s"didn't throw expected exception ${clazz.getCanonicalName}")
            }
            else true
      }
    if (threwExpectedException) {
      Succeeded
    }
    else {
      fail(s"didn't throw expected exception ${clazz.getCanonicalName}")
    }
  }

}
