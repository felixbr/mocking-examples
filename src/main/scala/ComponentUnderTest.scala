import cats.effect._
import io.chrisdavenport.log4cats.Logger

import scala.concurrent.duration._

trait ComponentUnderTest {
  def doStuff: IO[Unit]
}

/**
  * This is the component we want to write tests for to make sure we don't forget to send the 'Important Message' to the
  * external service.
  *
  * Also we want to test that our resilience is decent because the external service is terribly unreliable and we don't
  * want it to impact our stuff.
  */
final class ComponentUnderTestImpl(
  externalService: ExternalService
)(implicit log: Logger[IO], timer: Timer[IO], cs: ContextShift[IO])
    extends ComponentUnderTest {

  override def doStuff: IO[Unit] =
    for {
      _ <- log.info("Starting")

      _ <- log.info("Making external call while guarding against crashes/delays")
      _ <- externalService.sendMessage("Important Message")
        .timeout(100.millis)
        .handleErrorWith { exc =>
          log.error(exc)(s"Failed to send message") *>
            IO.unit
        }

      _ <- log.info("Stopping")
    } yield ()
}
