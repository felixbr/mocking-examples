import cats.effect.{ContextShift, IO, Timer}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

trait IOSupport {
  implicit def logger: Logger[IO] = Slf4jLogger.getLoggerFromName("test-logger")

  implicit lazy val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(2))

  implicit lazy val timer: Timer[IO]     = IO.timer(ec)
  implicit lazy val cs: ContextShift[IO] = IO.contextShift(ec)
}
