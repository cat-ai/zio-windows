package zio.windows

import zio._
import zio.blocking.Blocking
import zio.duration.durationInt
import zio.test.Assertion.equalTo
import zio.test._
import zio.test.environment.TestEnvironment

import java.io.IOException

object ZioWindowsTest extends DefaultRunnableSpec {

  val windowsMemory: ZLayer[Blocking, TestFailure[IOException], WindowsMemory] = memory.mapError(TestFailure.fail)
  val windowsIO:     ZLayer[Blocking, TestFailure[IOException], WindowsIO]     = io.mapError(TestFailure.fail)

  override def spec: Spec[TestEnvironment, TestFailure[Exception], TestSuccess] =
    suite("Common Windows Tests")(
      testM("memcpy") {
        for {
          src    <- alloc(1 << 4)
          dest   <- alloc(1 << 4)
          copied <- WindowsMemory.memcpy(src, dest)
          result <- WindowsMemory.equal(src, copied)
        } yield assert(result)(equalTo(true))
      },
      testM("lastError") {
        for {
          _            <- lastError
          newLastError =  10
          _            <- setLastError(newLastError)
          n            <- lastError
        } yield assert(n == newLastError)(equalTo(true))
      }

    ).provideCustomLayerShared(windowsMemory ++ windowsIO ++ Blocking.live) @@ TestAspect.timeout(30.seconds)
}
