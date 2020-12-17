import cats.data.Chain
import cats.effect.IO
import org.scalatest.Outcome
import org.scalatest.wordspec.FixtureAnyWordSpec

import scala.concurrent.duration._

/**
  * Manual mocking can be extremely flexible and powerful but for most mundane test-cases, it is too verbose, so
  * people use libraries like Mockito, to cut down on boilerplate.
  *
  * Personally I only do this for very elaborate setups, e.g. for setting up a real webserver for each test-case which
  * let's you make assertions based on its accesslogs.
  */
class ManualMockSpec extends FixtureAnyWordSpec with IOSupport {

  final protected class FixtureParam {
    var sentMessages: Chain[String] = Chain.empty // like list but efficient appending

    val externalServiceMock: ExternalService = new ExternalService {
      // Instead of the real call we instead record the call with it's arguments
      override def sendMessage(msg: String): IO[Unit] = IO {
        sentMessages = sentMessages.append(msg)
      }

      override def fetchUserName(userId: Int): IO[String] = ???
    }

    val component = new ComponentUnderTestImpl(externalServiceMock)
  }

  "ComponentUnderTest" must {
    "call externalService with 'Important Message'" in { f =>
      f.component.doStuff.unsafeRunSync()

      assert(f.sentMessages == Chain("Important Message"))
    }
  }

  override protected def withFixture(test: OneArgTest): Outcome = withFixture(test.toNoArgTest(new FixtureParam))
}
