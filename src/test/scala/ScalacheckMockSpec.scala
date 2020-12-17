import cats.data.Chain
import cats.effect.IO
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.Outcome
import org.scalatest.wordspec.{AnyWordSpec, FixtureAnyWordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.duration._

/**
  * This uses ScalaCheck to do fault-injection. We simulate random crashes and delays of our `ExternalService` so we
  * can be sure that our code handles even those very unexpected and adverse dependencies.
  */
class ScalacheckMockSpec extends AnyWordSpec with IOSupport with ScalaCheckPropertyChecks {

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = 100)

  private lazy val genDelay: Gen[FiniteDuration] = Gen.chooseNum(10, 100).map(_.seconds)

  /**
    * Most people don't know that ScalaCheck can not only generate values but functions as well! They even return
    * always the same for the same arguments.
    *
    * You need `Cogen` instances for all argument types and `Arbitrary` instances for all return types of the function
    * you want to generate.
    */
  implicit private def arbUnstableIO[A: Arbitrary]: Arbitrary[IO[A]] = Arbitrary(
    // This is a neat way to do fault-injection
    Gen.frequency(
      4 -> arbitrary[A].map(IO.pure),
      1 -> IO.raiseError(new Exception("💥")), // random crashe
      1 -> genDelay.flatMap(delay => arbitrary[A].map(a => IO.pure(a).delayBy(delay))), // random delay
    )
  )

  private lazy val genUnstableExternalService: Gen[ExternalService] =
    for {
      sendMessageFn   <- arbitrary[String => IO[Unit]]
      fetchUserNameFn <- arbitrary[Int => IO[String]]
    } yield new ExternalService {
      override def sendMessage(msg: String): IO[Unit]     = sendMessageFn(msg)
      override def fetchUserName(userId: Int): IO[String] = fetchUserNameFn(userId)
    }

  "ComponentUnderTest" must {
    "not crash or be delayed" in {
      forAll(genUnstableExternalService) { unstableExternalService =>
        val component = new ComponentUnderTestImpl(unstableExternalService)

        component.doStuff.unsafeRunTimed(1.second).ensuring(_.isDefined, "'.doStuff' was delayed")
      }
    }
  }
}
